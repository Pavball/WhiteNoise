package pavball.hr.whitenoise.playback

import android.app.PendingIntent
import android.net.Uri
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import pavball.hr.whitenoise.R
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.domain.model.SoundMix
import pavball.hr.whitenoise.domain.usecase.GetCustomSoundUseCase
import pavball.hr.whitenoise.playback.PlaybackContract.MixSnapshot
import pavball.hr.whitenoise.playback.PlaybackContract.TimerSnapshot
import pavball.hr.whitenoise.ui.components.SettingsDataStore
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import java.io.File
import kotlin.math.ceil

/**
 * Owns the players, the media session (notification, lock screen, headset
 * buttons) and the sleep timer, so playback and the timer keep working when
 * the app is in the background or its UI has been closed.
 *
 * The selected sound plays on the session's player; sounds mixed on top of it
 * ("layers") each get their own player that follows its play/pause state.
 */
@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {

    private val getCustomSoundUseCase: GetCustomSoundUseCase by inject()
    private val settings: SettingsDataStore by inject()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var player: DozzzePlayer
    private var mediaSession: MediaSession? = null
    private val leadAnalyzer = SpectrumAnalyzer()

    // A sound the app asked for before it was in the playlist (e.g. just added).
    private var pendingSoundId: String? = null

    // Playlist sources: noise loops appear once generated, custom sounds come from the DB.
    private var noiseFiles: Map<String, File> = emptyMap()
    private var customItems: List<MediaItem> = emptyList()

    // --------------------------------------------------------------------
    // ||                 MIX STATE                                      ||
    // --------------------------------------------------------------------
    private class Layer(val player: ExoPlayer, val analyzer: SpectrumAnalyzer)

    private var leadVolume = 1f
    private val layerVolumes = LinkedHashMap<String, Float>() // what the user mixed in
    private val layers = LinkedHashMap<String, Layer>()       // players actually running
    private var fadeFactor = 1f                               // sleep-timer fade, scales every sound
    private var mixChangedByUser = false
    private var saveMixJob: Job? = null

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
        exoPlayer = buildPlayer(leadAnalyzer, isLead = true).apply { addListener(playerListener) }
        PlaybackSpectrum.register(leadAnalyzer)
        player = DozzzePlayer(exoPlayer)

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this).build()
                .apply { setSmallIcon(R.drawable.ic_notification) }
        )

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(SessionCallback())
            .apply { sessionActivity()?.let(::setSessionActivity) }
            .build()

        rebuildPlaylist()
        scope.launch {
            noiseFiles = withContext(Dispatchers.IO) {
                runCatching { NoiseGenerator.ensureLoops(this@PlaybackService) }.getOrDefault(emptyMap())
            }
            rebuildPlaylist()
        }
        scope.launch { restoreMix() }
        scope.launch {
            getCustomSoundUseCase().collect { customSounds ->
                customItems = customSounds.map(::toMediaItem)
                rebuildPlaylist()
                forgetDeletedLayers()
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        scope.cancel()
        timerJob = null
        exoPlayer.removeListener(playerListener)
        layers.values.forEach { it.release() }
        layers.clear()
        PlaybackSpectrum.unregister(leadAnalyzer)
        mediaSession?.run {
            release()
            player.release() // releases the wrapped ExoPlayer too
        }
        mediaSession = null
        super.onDestroy()
    }

    private fun buildPlayer(analyzer: SpectrumAnalyzer, isLead: Boolean): ExoPlayer =
        ExoPlayer.Builder(this, AnalyzingRenderersFactory(this, analyzer))
            // Only the main player takes audio focus; layers pause and resume with it.
            .setAudioAttributes(AUDIO_ATTRIBUTES, /* handleAudioFocus = */ isLead)
            .setHandleAudioBecomingNoisy(isLead)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
            .apply { repeatMode = Player.REPEAT_MODE_ONE }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            // The timer only counts down while sound is actually playing.
            if (isPlaying) resumeTimer() else pauseTimer()
            layers.values.forEach { it.player.playWhenReady = isPlaying }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            // Remember sounds picked from the notification / headset, or reached by auto-advance.
            if (mediaItem != null && (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK ||
                    reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO)
            ) {
                saveLastSound(mediaItem.mediaId)
            }
            updateRepeatMode()
            syncLayers() // the new main sound may have been a layer
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            // The last custom sound ended: start the custom sounds over from the first.
            if (playbackState == Player.STATE_ENDED && isCustom(exoPlayer.currentMediaItem?.mediaId)) {
                val first = (0 until exoPlayer.mediaItemCount)
                    .firstOrNull { isCustom(exoPlayer.getMediaItemAt(it).mediaId) }
                    ?: return
                exoPlayer.seekToDefaultPosition(first) // still playWhenReady, so it plays on
            }
        }
    }

    /**
     * Built-in sounds are seamless loops and repeat on their own. Custom sounds
     * (often songs or long recordings) play one after another instead: they sit at
     * the end of the playlist, so without repeat the player moves on to the next
     * custom sound, and [playerListener] wraps around after the last one.
     */
    private fun updateRepeatMode() {
        exoPlayer.repeatMode =
            if (isCustom(exoPlayer.currentMediaItem?.mediaId)) Player.REPEAT_MODE_OFF
            else Player.REPEAT_MODE_ONE
    }

    private fun isCustom(soundId: String?) = soundId != null && soundId !in BUILT_IN_KEYS

    // --------------------------------------------------------------------
    // ||                 PLAYLIST SECTION                               ||
    // --------------------------------------------------------------------
    private fun rebuildPlaylist() = syncPlaylist(builtInItems() + customItems)

    private fun builtInItems(): List<MediaItem> =
        MainScreenViewState.defaultSounds.mapNotNull { (label, key) ->
            val uri = BUILT_IN_SOUNDS[key]?.let { "android.resource://$packageName/$it" }
                ?: noiseFiles[key]?.let { Uri.fromFile(it).toString() }
                ?: return@mapNotNull null
            mediaItem(key, label, uri)
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
        updateRepeatMode()
        syncLayers() // layers waiting for their sound (e.g. a noise loop) can start now
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
    // ||                 MIX SECTION                                    ||
    // --------------------------------------------------------------------
    private fun setLeadVolume(volume: Float) {
        leadVolume = volume.coerceIn(0f, 1f)
        onMixChanged()
    }

    private fun setLayer(soundId: String, volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        when {
            clamped == 0f -> layerVolumes.remove(soundId)
            soundId in layerVolumes || layerVolumes.size < SoundMix.MAX_LAYERS -> layerVolumes[soundId] = clamped
        }
        onMixChanged()
    }

    private fun clearLayers() {
        layerVolumes.clear()
        onMixChanged()
    }

    private fun playMix(leadId: String, volume: Float, mixLayers: Map<String, Float>) {
        leadVolume = volume.coerceIn(0f, 1f)
        layerVolumes.clear()
        mixLayers.filter { (id, layerVolume) -> id != leadId && layerVolume > 0f }
            .entries.take(SoundMix.MAX_LAYERS)
            .forEach { (id, layerVolume) -> layerVolumes[id] = layerVolume.coerceIn(0f, 1f) }
        onMixChanged()
        playSound(leadId)
    }

    private suspend fun restoreMix() {
        val saved = settings.userSettingsFlow.first()
        if (mixChangedByUser) return // the app already changed the mix while this was loading
        leadVolume = saved.mixLeadVolume
        layerVolumes.clear()
        saved.mixLayers.entries.take(SoundMix.MAX_LAYERS).forEach { (id, volume) -> layerVolumes[id] = volume }
        onMixChanged(save = false)
    }

    private fun onMixChanged(save: Boolean = true) {
        if (save) {
            mixChangedByUser = true
            saveMix()
        }
        syncLayers()
        publishExtras()
    }

    private fun saveMix() {
        val lead = leadVolume
        val mix = LinkedHashMap(layerVolumes)
        saveMixJob?.cancel()
        saveMixJob = scope.launch {
            delay(SAVE_MIX_DELAY_MS) // a slider being dragged changes the mix many times a second
            settings.saveMix(lead, mix)
        }
    }

    /** Drops layers whose custom sound was deleted. */
    private fun forgetDeletedLayers() {
        val deleted = layerVolumes.keys.filter { isCustom(it) && indexOf(it) == C.INDEX_UNSET }
        if (deleted.isEmpty()) return
        deleted.forEach(layerVolumes::remove)
        onMixChanged()
    }

    /** Starts and stops layer players to match [layerVolumes], then applies every volume. */
    private fun syncLayers() {
        val leadId = exoPlayer.currentMediaItem?.mediaId
        val wanted = layerVolumes.keys.filter { it != leadId && indexOf(it) != C.INDEX_UNSET }
        (layers.keys - wanted.toSet()).forEach { id -> layers.remove(id)?.release() }
        for (id in wanted) {
            if (id in layers) continue
            val analyzer = SpectrumAnalyzer()
            val layerPlayer = buildPlayer(analyzer, isLead = false).apply {
                setMediaItem(exoPlayer.getMediaItemAt(indexOf(id)))
                volume = 0f // set properly by applyVolumes() below, before any audio is out
                prepare()
                playWhenReady = exoPlayer.isPlaying
            }
            PlaybackSpectrum.register(analyzer)
            layers[id] = Layer(layerPlayer, analyzer)
        }
        applyVolumes()
        refreshSubtitle()
    }

    private fun applyVolumes() {
        exoPlayer.volume = leadVolume * fadeFactor
        leadAnalyzer.gain = exoPlayer.volume
        for ((id, layer) in layers) {
            layer.player.volume = (layerVolumes[id] ?: 0f) * fadeFactor
            layer.analyzer.gain = layer.player.volume
        }
    }

    private fun setFadeFactor(factor: Float) {
        fadeFactor = factor
        applyVolumes()
    }

    private fun Layer.release() {
        PlaybackSpectrum.unregister(analyzer)
        player.release()
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
        setFadeFactor(1f)
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
        setFadeFactor(1f)
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
                    publishExtras()
                }
                setFadeFactor((remaining.toFloat() / fadeMs).coerceIn(0f, 1f))
            } else if (fadeStarted || fadeFactor < 1f) {
                // Timer was extended or fading was switched off mid-fade.
                fadeStarted = false
                setFadeFactor(1f)
                publishExtras()
            }
            refreshSubtitle()
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
        setFadeFactor(1f)
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
        publishExtras()
        refreshSubtitle()
    }

    // --------------------------------------------------------------------
    // ||                 SESSION SECTION                                ||
    // --------------------------------------------------------------------
    private fun sessionExtras() = Bundle().also { extras ->
        timerSnapshot().writeTo(extras)
        MixSnapshot(leadVolume, LinkedHashMap(layerVolumes)).writeTo(extras)
    }

    private fun publishExtras() {
        mediaSession?.setSessionExtras(sessionExtras())
    }

    /** Shows the layers and the sleep timer under the sound's name in the notification. */
    private fun refreshSubtitle() {
        val layerNames = layers.values.mapNotNull { it.player.currentMediaItem?.mediaMetadata?.title }
        val parts = buildList {
            if (layerNames.isNotEmpty()) {
                add(getString(R.string.notification_with_layers, layerNames.joinToString(", ")))
            }
            if (isTimerActive) {
                val minutesLeft = ceil(remainingMs() / MINUTE_MS.toDouble()).toInt().coerceAtLeast(1)
                add(getString(R.string.notification_timer_left, minutesLeft))
            }
        }
        player.subtitle = parts.joinToString(" · ").ifEmpty { null }
    }

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
                .setSessionExtras(sessionExtras())
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

                PlaybackContract.CMD_SET_LEAD_VOLUME ->
                    setLeadVolume(args.getFloat(PlaybackContract.ARG_VOLUME, 1f))

                PlaybackContract.CMD_SET_LAYER -> args.getString(PlaybackContract.ARG_SOUND_ID)?.let { id ->
                    setLayer(id, args.getFloat(PlaybackContract.ARG_VOLUME, 0f))
                }

                PlaybackContract.CMD_CLEAR_LAYERS -> clearLayers()

                PlaybackContract.CMD_PLAY_MIX -> args.getString(PlaybackContract.ARG_SOUND_ID)?.let { id ->
                    playMix(
                        leadId = id,
                        volume = args.getFloat(PlaybackContract.ARG_VOLUME, 1f),
                        mixLayers = MixSnapshot.layersFromArgs(args)
                    )
                }

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

        val BUILT_IN_KEYS = MainScreenViewState.defaultSounds.mapTo(HashSet()) { it.second }

        val AUDIO_ATTRIBUTES: AudioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        const val MINUTE_MS = 60_000L
        const val TICK_MS = 1_000L
        const val FADE_STEP_MS = 100L
        const val DEFAULT_FADE_SECONDS = 30
        const val DEFAULT_FADE_MS = DEFAULT_FADE_SECONDS * 1000L
        const val SAVE_MIX_DELAY_MS = 500L
    }
}
