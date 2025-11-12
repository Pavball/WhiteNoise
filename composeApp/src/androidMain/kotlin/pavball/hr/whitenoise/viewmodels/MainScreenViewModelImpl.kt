package pavball.hr.whitenoise.viewmodels

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import pavball.hr.whitenoise.domain.model.UserSound
import pavball.hr.whitenoise.ui.components.SettingsDataStore
import pavball.hr.whitenoise.ui.components.UserSettings
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal class MainScreenViewModelImpl(
    private val context: Context,
    private val settings: SettingsDataStore
) : MainScreenViewModel() {

    private val _userSounds = MutableStateFlow<List<UserSound>>(emptyList())
    override val userSounds: StateFlow<List<UserSound>> = _userSounds.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var currentSound: String? = null
    private var fadeOutEnabled: Boolean = true
    private var fadeStarted = false
    private var timerJob: Job? = null

    @OptIn(ExperimentalTime::class)
    private var endTime: Instant? = null

    private val resourceMap = mapOf(
        "rain" to pavball.hr.whitenoise.R.raw.rain,
        "ocean" to pavball.hr.whitenoise.R.raw.ocean_waves,
        "forest" to pavball.hr.whitenoise.R.raw.winter_forest,
        "thunder" to pavball.hr.whitenoise.R.raw.rain_thunder,
    )


    init {
        // Restore user settings
        runCommand {
            settings.userSettingsFlow.collectLatest { prefs ->
                updateState {
                    copy(
                        selectedSoundKey = prefs.lastSound ?: selectedSoundKey,
                        fadeEnabled = prefs.fadeEnabled,
                        fadeDuration = prefs.fadeDuration,
                        timerSelectedMinutes = prefs.timerMinutes,
                        themeMode = prefs.themeMode
                    )
                }
            }
        }

        runCommand {
            settings.userSoundsFlow.collectLatest { loaded ->
                _userSounds.value = loaded
            }
        }
    }

    //Custom User Sound
    override fun addUserSound(name: String, uri: String) {
        runCommand {
            val updated = _userSounds.value + UserSound(name, uri)
            _userSounds.value = updated
            settings.saveUserSounds(updated)
        }
    }

    override fun removeUserSound(uri: String) {
        runCommand {
            val updated = _userSounds.value.filterNot { it.uri == uri }
            _userSounds.value = updated
            settings.saveUserSounds(updated)
        }
    }

    // --- Playback ---
    override fun playSound(soundId: String) {
        runCommand {
            updateState { copy(isLoading = true, currentSound = soundId) }

            runOnMain {
                val uri = resolveSoundUri(soundId) ?: return@runOnMain

                val sameSound = currentSound == soundId && exoPlayer != null
                if (sameSound) {
                    // already prepared — just resume
                    exoPlayer?.playWhenReady = true
                    exoPlayer?.play()
                } else {
                    // replace existing player
                    exoPlayer?.release()

                    val player = ExoPlayer.Builder(context).build().also {
                        val mediaItem = MediaItem.fromUri(uri)
                        it.setMediaItem(mediaItem)
                        it.prepare()
                        it.play()
                    }

                    exoPlayer = player
                    currentSound = soundId
                }
            }

            updateState { copy(isPlaying = true, isLoading = false) }

            // persist selected sound to user settings (optional)
            runCommand {
                settings.saveSettings(getCurrentState().toUserSettings())
            }
        }
    }

    override fun pauseSound() {
        runCommand {
            runOnMain { exoPlayer?.pause() }
            updateState { copy(isPlaying = false) }
        }
    }

    override fun stopSound() {
        cancelTimer()
        runCommand {
            runOnMain {
                exoPlayer?.pause()
                exoPlayer?.seekTo(0)
            }
            updateState { copy(isPlaying = false) }
        }
    }

    override fun updateSelectedSoundKey(selectedSoundKey: String) {
        runCommand {
            updateState { copy(selectedSoundKey = selectedSoundKey) }
            settings.saveSettings(getCurrentState().toUserSettings())
        }
    }

    // --- Timer ---
    @OptIn(ExperimentalTime::class)
    override fun startTimer(minutes: Int, fadeOutEnabled: Boolean) {
        cancelTimer()
        this.fadeOutEnabled = fadeOutEnabled

        val totalTimeMs = minutes * 60 * 1000L
        var remainingTimeMs = totalTimeMs
        fadeStarted = false
        endTime = Clock.System.now().plus(remainingTimeMs.milliseconds)

        timerJob = runCommand {
            while (remainingTimeMs > 0) {
                delay(1.seconds)
                val remaining = endTime?.let { it - Clock.System.now() } ?: break
                remainingTimeMs = remaining.inWholeMilliseconds.coerceAtLeast(0)

                if (fadeOutEnabled && !fadeStarted && remainingTimeMs <= 30_000L) {
                    fadeStarted = true
                    fadeOutVolume()
                }

                updateState {
                    copy(
                        remainingTime = remainingTimeMs,
                        totalTime = totalTimeMs,
                        fadeStarted = fadeStarted
                    )
                }
            }

            if (remainingTimeMs <= 0) {
                stopSound()
                updateState { copy(timerFinished = true) }
            }
        }

        // Save timer state
        runCommand {
            settings.saveSettings(getCurrentState().toUserSettings())
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        endTime = null

        runCommand {
            updateState { copy(isPlaying = false) }
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun resumeTimer(onFadeStart: () -> Unit, onTimerFinished: () -> Unit) {
        val state = getCurrentState()
        val remainingMs = state.remainingTime ?: return

        endTime = Clock.System.now().plus(remainingMs.milliseconds)

        timerJob = runCommand {
            var remainingTimeMs = remainingMs
            fadeStarted = state.fadeStarted

            runOnMain {
                if (exoPlayer == null && currentSound != null) {
                    // Try to (re)prepare the player for the currentSound
                    val uri = resolveSoundUri(currentSound!!)
                    if (uri != null) {
                        exoPlayer = ExoPlayer.Builder(context).build().also {
                            val mediaItem = MediaItem.fromUri(uri)
                            it.setMediaItem(mediaItem)
                            it.prepare()
                            it.play()
                        }
                    }
                } else {
                    // if player exists, resume playback
                    exoPlayer?.playWhenReady = true
                    exoPlayer?.play()
                }
            }

            updateState { copy(isPlaying = true) }

            while (remainingTimeMs > 0) {
                delay(1.seconds)
                val remaining = endTime?.let { it - Clock.System.now() } ?: break
                remainingTimeMs = remaining.inWholeMilliseconds.coerceAtLeast(0)

                if (fadeOutEnabled && !fadeStarted && remainingTimeMs <= 30_000L) {
                    fadeStarted = true
                    onFadeStart()
                    fadeOutVolume()
                }

                updateState {
                    copy(
                        remainingTime = remainingTimeMs,
                        fadeStarted = fadeStarted
                    )
                }
            }

            if (remainingTimeMs <= 0) {
                onTimerFinished()
                stopSound()
                updateState { copy(timerFinished = true) }
            }
        }
    }


    @OptIn(ExperimentalTime::class)
    override fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        endTime = null
        fadeStarted = false

        runCommand {
            updateState {
                copy(
                    isPlaying = false,
                    remainingTime = null,
                    totalTime = null,
                    fadeStarted = false,
                    timerFinished = false
                )
            }
        }
    }

    override fun updateSelectedTimer(minutes: Int) {
        runCommand {
            updateState { copy(timerSelectedMinutes = minutes) }
            settings.saveSettings(getCurrentState().toUserSettings())
        }
    }

    override fun close() {
        super.close()
        exoPlayer?.release()
        exoPlayer = null
    }

    private fun fadeOutVolume() = runCommand {
        val fadeDurationMs = 30_000L
        val steps = 30
        val delayPerStep = fadeDurationMs / steps
        val volumeStep = 1f / steps

        for (i in 1..steps) {
            val newVolume = (1f - i * volumeStep).coerceIn(0f, 1f)
            withContext(Dispatchers.Main.immediate) {
                exoPlayer?.volume = newVolume
            }
            delay(delayPerStep)
        }

        withContext(Dispatchers.Main.immediate) {
            stopSound()
        }
    }

    override fun saveThemeModeToUserPrefs(themeMode: String) {
        runCommand {
            updateState { copy(themeMode = themeMode) }
            settings.saveSettings(getCurrentState().toUserSettings())
        }
    }

    private fun MainScreenViewState.toUserSettings() = UserSettings(
        lastSound = currentSound ?: selectedSoundKey,
        timerMinutes = timerSelectedMinutes,
        fadeEnabled = fadeEnabled,
        fadeDuration = fadeDuration,
        themeMode = themeMode
    )

    private fun resolveSoundUri(soundId: String): Uri? {
        // user-provided URIs are passed as-is
        if (soundId.startsWith("content://") || soundId.startsWith("file://") || soundId.startsWith("http")) {
            return Uri.parse(soundId)
        }

        // built-in resource ids
        val resId = resourceMap[soundId]
        return if (resId != null) {
            Uri.parse("android.resource://${context.packageName}/$resId")
        } else {
            // If the soundId looks like a Uri string but without scheme, try parsing
            return try {
                Uri.parse(soundId).takeIf { it.scheme != null }
            } catch (e: Exception) {
                null
            }
        }
    }

}

private suspend fun runOnMain(block: () -> Unit) {
    withContext(Dispatchers.Main) { block() }
}
