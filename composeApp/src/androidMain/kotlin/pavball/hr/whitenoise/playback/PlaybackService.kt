package pavball.hr.whitenoise.playback

import android.app.PendingIntent
import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import pavball.hr.whitenoise.R
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.domain.usecase.GetCustomSoundUseCase
import pavball.hr.whitenoise.playback.PlaybackContract.TimerSnapshot
import pavball.hr.whitenoise.ui.components.SettingsDataStore
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import kotlin.math.ceil

/**
 * Owns the player, the media session (notification, lock screen, headset
 * buttons) and the sleep timer, so playback and the timer keep working when
 * the app is in the background or its UI has been closed.
 */
@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {

    private val getCustomSoundUseCase: GetCustomSoundUseCase by inject()
    private val settings: SettingsDataStore by inject()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var player: DozzzePlayer
    private var mediaSession: MediaSession? = null

    // A sound the app asked for before it was in the playlist (e.g. just added).
    private var pendingSoundId: String? = null

    // --------------------------------------------------------------------
    // ||                 SLEEP TIMER STATE                              ||
    // --------------------------------------------------------------------
    private var timerTotalMs = 0L
    private var timerRemainingMs = 0L      // valid while paused
    private var timerEndElapsed = 0L       // valid while running
    private var timerRunning = false
    private var fadeEnabled = true
    private var fadeMs = DEFAULT_FADE_MS
    private var fadeStarted = false
    private var timerJob: Job? = null

    private val isTimerActive get() = timerTotalMs > 0

    // --------------------------------------------------------------------
    // ||                 LIFECYCLE SECTION                              ||
    // --------------------------------------------------------------------
    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                addListener(playerListener)
            }
        player = DozzzePlayer(exoPlayer)

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this).build()
                .apply { setSmallIcon(R.drawable.ic_notification) }
        )

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(SessionCallback())
            .apply { sessionActivity()?.let(::setSessionActivity) }
            .build()

        syncPlaylist(builtInItems())
        scope.launch {
            getCustomSoundUseCase().collect { customSounds ->
                syncPlaylist(builtInItems() + customSounds.map(::toMediaItem))
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        scope.cancel()
        timerJob = null
        exoPlayer.removeListener(playerListener)
        mediaSession?.run {
            release()
            player.release() // releases the wrapped ExoPlayer too
        }
        mediaSession = null
        super.onDestroy()
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            // The timer only counts down while sound is actually playing.
            if (isPlaying) resumeTimer() else pauseTimer()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            // Remember sounds picked from the notification / headset as well.
            if (mediaItem != null && reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
                saveLastSound(mediaItem.mediaId)
            }
        }
    }

    // --------------------------------------------------------------------
    // ||                 PLAYLIST SECTION                               ||
    // --------------------------------------------------------------------
    private fun builtInItems(): List<MediaItem> =
        MainScreenViewState.defaultSounds.mapNotNull { (label, key) ->
            val resId = BUILT_IN_SOUNDS[key] ?: return@mapNotNull null
            mediaItem(key, label, "android.resource://$packageName/$resId")
        }

    private fun toMediaItem(sound: CustomSound) =
        mediaItem(sound.id, sound.displayName, sound.uri)

    private fun mediaItem(id: String, title: String, uri: String) =
        MediaItem.Builder()
            .setMediaId(id)
            .setUri(uri.toUri())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(getString(R.string.app_name))
                    .setIsPlayable(true)
                    .setIsBrowsable(false)
                    .build()
            )
            .build()

    /**
     * Brings the player's playlist in line with [desired] using in-place
     * edits, so the sound that is playing is never interrupted.
     */
    private fun syncPlaylist(desired: List<MediaItem>) {
        val desiredIds = desired.mapTo(HashSet()) { it.mediaId }
        for (i in exoPlayer.mediaItemCount - 1 downTo 0) {
            if (exoPlayer.getMediaItemAt(i).mediaId !in desiredIds) exoPlayer.removeMediaItem(i)
        }
        desired.forEachIndexed { target, item ->
            val existing = (target until exoPlayer.mediaItemCount)
                .firstOrNull { exoPlayer.getMediaItemAt(it).mediaId == item.mediaId }
            when {
                existing == null -> exoPlayer.addMediaItem(target, item)
                else -> {
                    if (existing != target) exoPlayer.moveMediaItem(existing, target)
                    if (exoPlayer.getMediaItemAt(target).mediaMetadata.title != item.mediaMetadata.title) {
                        exoPlayer.replaceMediaItem(target, item) // renamed
                    }
                }
            }
        }
        pendingSoundId?.let { id -> if (indexOf(id) != C.INDEX_UNSET) playSound(id) }
    }

    private fun indexOf(soundId: String): Int =
        (0 until exoPlayer.mediaItemCount)
            .firstOrNull { exoPlayer.getMediaItemAt(it).mediaId == soundId }
            ?: C.INDEX_UNSET

    // --------------------------------------------------------------------
    // ||                 PLAYBACK SECTION                               ||
    // --------------------------------------------------------------------
    private fun playSound(soundId: String) {
        val index = indexOf(soundId)
        if (index == C.INDEX_UNSET) {
            pendingSoundId = soundId
            return
        }
        pendingSoundId = null
        if (index != exoPlayer.currentMediaItemIndex || exoPlayer.playbackState == Player.STATE_ENDED) {
            exoPlayer.seekToDefaultPosition(index)
        }
        if (exoPlayer.playbackState == Player.STATE_IDLE) exoPlayer.prepare() // also retries after an error
        exoPlayer.play()
        saveLastSound(soundId)
    }

    private fun saveLastSound(soundId: String) {
        scope.launch { settings.saveLastSound(soundId) }
    }

    // --------------------------------------------------------------------
    // ||                 SLEEP TIMER SECTION                            ||
    // --------------------------------------------------------------------
    private fun startTimer(minutes: Int, fade: Boolean, fadeSeconds: Int) {
        if (minutes <= 0) {
            cancelTimer()
            return
        }
        resetTimer()
        setFade(fade, fadeSeconds)
        exoPlayer.volume = 1f
        timerTotalMs = minutes * MINUTE_MS
        timerRemainingMs = timerTotalMs
        if (exoPlayer.isPlaying) resumeTimer() else onTimerChanged()
    }

    private fun extendTimer(minutes: Int) {
        if (minutes <= 0) return
        if (!isTimerActive) {
            startTimer(minutes, fadeEnabled, (fadeMs / 1000).toInt())
            return
        }
        val extraMs = minutes * MINUTE_MS
        timerTotalMs += extraMs
        if (timerRunning) timerEndElapsed += extraMs else timerRemainingMs += extraMs
        onTimerChanged() // a running loop restores the volume if it was fading
    }

    private fun cancelTimer() {
        resetTimer()
        exoPlayer.volume = 1f
        onTimerChanged()
    }

    private fun setFade(enabled: Boolean, seconds: Int) {
        fadeEnabled = enabled
        fadeMs = (seconds * 1000L).coerceAtLeast(1000L)
    }

    private fun resumeTimer() {
        if (!isTimerActive || timerRunning) return
        timerEndElapsed = SystemClock.elapsedRealtime() + timerRemainingMs
        timerRunning = true
        timerJob = scope.launch { runTimerLoop() }
        onTimerChanged()
    }

    private fun pauseTimer() {
        if (!timerRunning) return
        timerRemainingMs = remainingMs()
        timerRunning = false
        timerJob?.cancel()
        timerJob = null
        onTimerChanged()
    }

    private suspend fun CoroutineScope.runTimerLoop() {
        while (isActive) {
            val remaining = remainingMs()
            if (remaining <= 0L) {
                finishTimer()
                return
            }
            val inFadeWindow = fadeEnabled && remaining <= fadeMs
            if (inFadeWindow) {
                if (!fadeStarted) {
                    fadeStarted = true
                    publishTimer()
                }
                exoPlayer.volume = (remaining.toFloat() / fadeMs).coerceIn(0f, 1f)
            } else if (fadeStarted || exoPlayer.volume < 1f) {
                // Timer was extended or fading was switched off mid-fade.
                fadeStarted = false
                exoPlayer.volume = 1f
                publishTimer()
            }
            updateNotificationSubtitle(remaining)
            delay(
                when {
                    inFadeWindow -> FADE_STEP_MS
                    fadeEnabled -> minOf(TICK_MS, remaining - fadeMs)
                    else -> minOf(TICK_MS, remaining)
                }
            )
        }
    }

    private fun finishTimer() {
        resetTimer()
        exoPlayer.pause()
        exoPlayer.volume = 1f
        onTimerChanged()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        timerTotalMs = 0L
        timerRemainingMs = 0L
        timerEndElapsed = 0L
        timerRunning = false
        fadeStarted = false
    }

    private fun remainingMs(): Long =
        if (timerRunning) (timerEndElapsed - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
        else timerRemainingMs

    private fun timerSnapshot() = TimerSnapshot(
        totalMs = timerTotalMs,
        remainingMs = remainingMs(),
        isRunning = timerRunning,
        snapshotElapsedRealtime = SystemClock.elapsedRealtime(),
        fadeStarted = fadeStarted
    )

    private fun onTimerChanged() {
        publishTimer()
        updateNotificationSubtitle(remainingMs())
    }

    private fun publishTimer() {
        mediaSession?.setSessionExtras(timerSnapshot().toBundle())
    }

    private fun updateNotificationSubtitle(remainingMs: Long) {
        player.subtitle = if (isTimerActive) {
            val minutesLeft = ceil(remainingMs / MINUTE_MS.toDouble()).toInt().coerceAtLeast(1)
            getString(R.string.notification_timer_left, minutesLeft)
        } else null
    }

    // --------------------------------------------------------------------
    // ||                 SESSION SECTION                                ||
    // --------------------------------------------------------------------
    private fun sessionActivity(): PendingIntent? {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: return null
        return PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private inner class SessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val builder = MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setSessionExtras(timerSnapshot().toBundle())
            // Only this app (its UI and its own notification) may use the custom commands.
            if (controller.packageName == packageName) {
                val commands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                PlaybackContract.ALL_COMMANDS.forEach { commands.add(PlaybackContract.command(it)) }
                builder.setAvailableSessionCommands(commands.build())
            }
            return builder.build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            when (customCommand.customAction) {
                PlaybackContract.CMD_PLAY_SOUND ->
                    args.getString(PlaybackContract.ARG_SOUND_ID)?.let(::playSound)

                PlaybackContract.CMD_START_TIMER -> startTimer(
                    minutes = args.getInt(PlaybackContract.ARG_MINUTES),
                    fade = args.getBoolean(PlaybackContract.ARG_FADE_ENABLED, true),
                    fadeSeconds = args.getInt(PlaybackContract.ARG_FADE_SECONDS, DEFAULT_FADE_SECONDS)
                )

                PlaybackContract.CMD_EXTEND_TIMER ->
                    extendTimer(args.getInt(PlaybackContract.ARG_MINUTES))

                PlaybackContract.CMD_CANCEL_TIMER -> cancelTimer()

                PlaybackContract.CMD_SET_FADE -> setFade(
                    enabled = args.getBoolean(PlaybackContract.ARG_FADE_ENABLED, true),
                    seconds = args.getInt(PlaybackContract.ARG_FADE_SECONDS, DEFAULT_FADE_SECONDS)
                )

                else -> return Futures.immediateFuture(SessionResult(SessionError.ERROR_NOT_SUPPORTED))
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }

    private companion object {
        val BUILT_IN_SOUNDS = mapOf(
            "rain" to R.raw.rain,
            "ocean" to R.raw.ocean_waves,
            "forest" to R.raw.winter_forest,
            "thunder" to R.raw.rain_thunder
        )

        const val MINUTE_MS = 60_000L
        const val TICK_MS = 1_000L
        const val FADE_STEP_MS = 100L
        const val DEFAULT_FADE_SECONDS = 30
        const val DEFAULT_FADE_MS = DEFAULT_FADE_SECONDS * 1000L
    }
}
