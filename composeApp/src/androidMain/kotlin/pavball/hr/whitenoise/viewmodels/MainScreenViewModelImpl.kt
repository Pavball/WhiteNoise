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
import pavball.hr.whitenoise.domain.model.CustomSound
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

    private val _customSounds = MutableStateFlow<List<CustomSound>>(emptyList())
    override val userSounds: StateFlow<List<CustomSound>> = _customSounds.asStateFlow()

    private val _pendingRename = MutableStateFlow<CustomSound?>(null)
    override val pendingRename: StateFlow<CustomSound?> = _pendingRename.asStateFlow()

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
        "thunder" to pavball.hr.whitenoise.R.raw.rain_thunder
    )

    init {
        // load persisted user settings into state
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

        // load persisted custom sounds into _customSounds
        runCommand {
            settings.userSoundsFlow.collectLatest { loaded ->
                _customSounds.value = loaded
            }
        }
    }

    // -------------------- custom sound ops --------------------
    override fun addUserSound(name: String, uri: String) {
        runCommand {
            // prepare sound
            val sound = CustomSound(id = uri, displayName = name)

            // set pending rename immediately so UI shows dialog without delay
            runOnMain { _pendingRename.value = sound }

            // update in-memory list first (so UI lists it right away)
            val updated = _customSounds.value + sound
            _customSounds.value = updated

            // persist on IO and await completion
            withContext(Dispatchers.IO) {
                settings.saveUserSounds(updated)
            }
        }
    }

    override fun renameCustomSound(id: String, newName: String) {
        runCommand {
            val updated = _customSounds.value.map { if (it.id == id) it.copy(displayName = newName) else it }
            _customSounds.value = updated

            // persist on IO
            withContext(Dispatchers.IO) {
                settings.saveUserSounds(updated)
            }

            // clear pending rename if it was this sound
            if (_pendingRename.value?.id == id) _pendingRename.value = null
        }
    }

    override fun removeUserSound(id: String) {
        runCommand {
            val updated = _customSounds.value.filterNot { it.id == id }
            _customSounds.value = updated

            withContext(Dispatchers.IO) {
                settings.saveUserSounds(updated)
            }

            // if removed was pending rename clear it
            if (_pendingRename.value?.id == id) _pendingRename.value = null
        }
    }

    override fun clearPendingRename() {
        runCommand {
            _pendingRename.value = null
        }
    }

    // -------------------- playback --------------------
    override fun playSound(soundId: String) {
        runCommand {
            updateState { copy(isLoading = true, currentSound = soundId, isCleared = false) }

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

            // persist selected sound to user settings (on IO, safely)
            withContext(Dispatchers.IO) {
                saveUserSettings()
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
            updateState { copy(isPlaying = false, isCleared = true) }
        }
    }

    override fun updateSelectedSoundKey(selectedSoundKey: String) {
        runCommand {
            updateState { copy(selectedSoundKey = selectedSoundKey) }

            if (!selectedSoundKey.startsWith("content://")) {
                // Only save default sounds
                saveUserSettings()
            }
        }
    }

    // -------------------- timer (unchanged behavior) --------------------
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

        runCommand {
            // ensure persisted safely
            withContext(Dispatchers.IO) {
                saveUserSettings()
            }
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
            withContext(Dispatchers.IO) {
                saveUserSettings()
            }
        }
    }

    override fun saveThemeModeToUserPrefs(themeMode: String) {
        runCommand {
            updateState { copy(themeMode = themeMode) }

            // Save on IO dispatcher to avoid blocking UI
            withContext(Dispatchers.IO) {
                saveUserSettings()
            }
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

    // IMPORTANT: do not persist content:// URIs as lastSound.
    private fun MainScreenViewState.toUserSettings(): UserSettings {
        return UserSettings(
            lastSound =
                if (!selectedSoundKey.startsWith("content://")) selectedSoundKey
                else "rain", // fallback so we never save custom URIs

            timerMinutes = timerSelectedMinutes,
            fadeEnabled = fadeEnabled,
            fadeDuration = fadeDuration,
            themeMode = themeMode
        )
    }

    private fun saveUserSettings() = runCommand {
        settings.saveSettings(getCurrentState().toUserSettings())
    }

    private fun resolveSoundUri(soundId: String): Uri? {
        if (soundId.startsWith("content://") || soundId.startsWith("file://") || soundId.startsWith("http")) {
            return Uri.parse(soundId)
        }

        val resId = resourceMap[soundId]
        return if (resId != null) {
            Uri.parse("android.resource://${context.packageName}/$resId")
        } else {
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
