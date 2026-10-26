package pavball.hr.whitenoise.viewmodels

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.domain.usecase.DeleteCustomSoundUseCase
import pavball.hr.whitenoise.domain.usecase.GetCustomSoundUseCase
import pavball.hr.whitenoise.domain.usecase.InsertCustomSoundUseCase
import pavball.hr.whitenoise.domain.usecase.UpdateCustomSoundColorUseCase
import pavball.hr.whitenoise.domain.usecase.UpdateCustomSoundUseCase
import pavball.hr.whitenoise.playback.PlaybackContract
import pavball.hr.whitenoise.playback.PlaybackContract.TimerSnapshot
import pavball.hr.whitenoise.playback.PlaybackService
import pavball.hr.whitenoise.ui.components.SettingsDataStore
import pavball.hr.whitenoise.ui.components.utils.CustomSoundPalette
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Playback and the sleep timer live in [PlaybackService] so they survive the
 * UI being closed. This ViewModel talks to it through a [MediaController] and
 * mirrors its state into the view state.
 */
internal class MainScreenViewModelImpl(
    private val context: Context,
    private val settings: SettingsDataStore,
    private val getCustomSoundUseCase: GetCustomSoundUseCase,
    private val updateCustomSoundUseCase: UpdateCustomSoundUseCase,
    private val deleteCustomSoundUseCase: DeleteCustomSoundUseCase,
    private val insertCustomSoundUseCase: InsertCustomSoundUseCase,
    private val updateCustomSoundColorUseCase: UpdateCustomSoundColorUseCase
) : MainScreenViewModel() {

    private val _pendingRename = MutableStateFlow<CustomSound?>(null)
    override val pendingRename: StateFlow<CustomSound?> =
        _pendingRename.asStateFlow()

    private var controller: MediaController? = null
    private val pendingControllerActions = mutableListOf<(MediaController) -> Unit>()
    private var timerSnapshot: TimerSnapshot? = null
    private var timerTickerJob: Job? = null

    private val controllerFuture = MediaController.Builder(
        context,
        SessionToken(context, ComponentName(context, PlaybackService::class.java))
    )
        .setListener(object : MediaController.Listener {
            override fun onExtrasChanged(controller: MediaController, extras: Bundle) {
                applyTimerExtras(extras)
            }
        })
        .buildAsync()

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            syncPlaybackState()
        }
    }

    // --------------------------------------------------------------------
    // ||                 INITIALIZE SECTION                               ||
    // --------------------------------------------------------------------
    init {
        // collect settings
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
        // collect custom sounds from SQLDelight via usecase
        runCommand {
            getCustomSoundUseCase().collectLatest { list ->
                updateState {
                    copy(
                        customSounds = list
                    )
                }
            }
        }
        controllerFuture.addListener(
            { onControllerReady() },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun onControllerReady() {
        if (!viewModelScope.isActive) return // closed before the connection finished
        val connected = runCatching { controllerFuture.get() }.getOrNull() ?: return
        controller = connected
        connected.addListener(playerListener)
        applyTimerExtras(connected.sessionExtras)
        syncPlaybackState()
        pendingControllerActions.forEach { it(connected) }
        pendingControllerActions.clear()
    }

    // --------------------------------------------------------------------
    // ||                 CUSTOM SOUND SECTION                           ||
    // --------------------------------------------------------------------
    @OptIn(ExperimentalTime::class)
    override fun addUserSound(displayName: String, uri: String) {
        runCommand {
            val id = uri // the persisted content uri is unique per file
            val ts = Clock.System.now().toEpochMilliseconds()
            val colorId = CustomSoundPalette.colorFor(getCurrentState().customSounds.size)
            insertCustomSoundUseCase(id, displayName, uri, ts, colorId)
            // set pending rename so UI opens dialog
            _pendingRename.value = CustomSound(
                id = id,
                displayName = displayName,
                uri = uri,
                addedAt = ts,
                colorId = colorId
            )
        }
    }

    override fun renameCustomSound(id: String, newName: String) {
        runCommand {
            updateCustomSoundUseCase(id, newName)
            // UI will refresh from DB flow
            _pendingRename.value = null
        }
    }

    override fun updateCustomSoundColor(id: String, colorId: String) {
        runCommand {
            updateCustomSoundColorUseCase(id, colorId)
            // UI will refresh from DB flow
            _pendingRename.value = null
        }
    }

    override fun removeUserSound(id: String) {
        runCommand {
            deleteCustomSoundUseCase(id)
            if (_pendingRename.value?.id == id) _pendingRename.value = null
        }
    }

    override fun clearPendingRename() {
        _pendingRename.value = null
    }

    // --------------------------------------------------------------------
    // ||                 PLAYBACK SECTION                               ||
    // --------------------------------------------------------------------
    override fun playSound(soundId: String) {
        val state = getCurrentState()
        runCommand { updateState { copy(selectedSoundKey = soundId, playbackError = false) } }
        sendCommand(
            PlaybackContract.CMD_PLAY_SOUND,
            bundleOf(PlaybackContract.ARG_SOUND_ID to soundId)
        )
        // Start the chosen sleep timer unless one is already counting down.
        if (state.remainingTime == null && state.timerSelectedMinutes > 0) {
            startTimer(state.timerSelectedMinutes)
        }
    }

    override fun togglePlayPause() {
        val state = getCurrentState()
        if (state.isPlaying) {
            withController { it.pause() }
        } else {
            playSound(state.selectedSoundKey)
        }
    }

    override fun skipToNext() = skipBy(1)

    override fun skipToPrevious() = skipBy(-1)

    private fun skipBy(step: Int) {
        val state = getCurrentState()
        val ids = state.playlistIds
        if (ids.isEmpty()) return
        val current = ids.indexOf(state.selectedSoundKey).coerceAtLeast(0)
        playSound(ids[(current + step + ids.size) % ids.size])
    }

    private fun syncPlaybackState() {
        val c = controller ?: return
        val hasMedia = c.playbackState != Player.STATE_IDLE && c.currentMediaItem != null
        runCommand {
            updateState {
                copy(
                    isPlaying = c.playWhenReady && hasMedia && c.playbackState != Player.STATE_ENDED,
                    isLoading = c.playWhenReady && c.playbackState == Player.STATE_BUFFERING,
                    playbackError = c.playerError != null,
                    // follow sounds changed from the notification or headset
                    selectedSoundKey = if (hasMedia) c.currentMediaItem?.mediaId ?: selectedSoundKey
                    else selectedSoundKey
                )
            }
        }
    }

    // --------------------------------------------------------------------
    // ||                 TIMER SECTION                                  ||
    // --------------------------------------------------------------------
    override fun updateSelectedTimer(minutes: Int) {
        val state = getCurrentState()
        runCommand {
            updateState { copy(timerSelectedMinutes = minutes) }
            settings.saveTimerMinutes(minutes)
        }
        when {
            minutes == 0 -> if (state.remainingTime != null) cancelTimer()
            // Picking a new duration while a sound or timer is going restarts the timer.
            state.isPlaying || state.remainingTime != null -> startTimer(minutes)
        }
    }

    override fun extendTimer(minutes: Int) {
        sendCommand(
            PlaybackContract.CMD_EXTEND_TIMER,
            bundleOf(PlaybackContract.ARG_MINUTES to minutes)
        )
    }

    override fun cancelTimer() {
        sendCommand(PlaybackContract.CMD_CANCEL_TIMER)
    }

    private fun startTimer(minutes: Int) {
        val state = getCurrentState()
        sendCommand(
            PlaybackContract.CMD_START_TIMER,
            bundleOf(
                PlaybackContract.ARG_MINUTES to minutes,
                PlaybackContract.ARG_FADE_ENABLED to state.fadeEnabled,
                PlaybackContract.ARG_FADE_SECONDS to state.fadeDuration
            )
        )
    }

    private fun applyTimerExtras(extras: Bundle) {
        val snapshot = TimerSnapshot.fromBundle(extras)
        timerSnapshot = snapshot
        timerTickerJob?.cancel()
        timerTickerJob = null
        renderTimer()
        if (snapshot?.isRunning == true) {
            // The service only publishes changes; count down locally in between.
            timerTickerJob = runCommand {
                while (isActive) {
                    delay(TICK_MS - snapshot.remainingMsNow() % TICK_MS)
                    renderTimer()
                }
            }
        }
    }

    private fun renderTimer() {
        val snapshot = timerSnapshot
        runCommand {
            updateState {
                copy(
                    remainingTime = snapshot?.remainingMsNow(),
                    totalTime = snapshot?.totalMs,
                    isTimerRunning = snapshot?.isRunning == true,
                    fadeStarted = snapshot?.fadeStarted == true
                )
            }
        }
    }

    // --------------------------------------------------------------------
    // ||                 UPDATE SECTION                                 ||
    // --------------------------------------------------------------------
    override fun updateFadeEnabled(enabled: Boolean) {
        runCommand {
            updateState { copy(fadeEnabled = enabled) }
            settings.saveFadeEnabled(enabled)
        }
        sendFadeSettings(enabled, getCurrentState().fadeDuration)
    }

    override fun saveFadeOutTimeToUserPrefs(fadeTime: Int) {
        runCommand {
            updateState { copy(fadeDuration = fadeTime) }
            settings.saveFadeDuration(fadeTime)
        }
        sendFadeSettings(getCurrentState().fadeEnabled, fadeTime)
    }

    override fun saveThemeModeToUserPrefs(themeMode: String) {
        val mode = themeMode.uppercase(Locale.ROOT)
        runCommand {
            updateState { copy(themeMode = mode) }
            settings.saveThemeMode(mode)
        }
    }

    // --------------------------------------------------------------------
    // ||                 HELPER FUNCTIONS SECTION                       ||
    // --------------------------------------------------------------------
    private fun sendFadeSettings(enabled: Boolean, seconds: Int) {
        sendCommand(
            PlaybackContract.CMD_SET_FADE,
            bundleOf(
                PlaybackContract.ARG_FADE_ENABLED to enabled,
                PlaybackContract.ARG_FADE_SECONDS to seconds
            )
        )
    }

    private fun sendCommand(action: String, args: Bundle = Bundle.EMPTY) {
        withController { it.sendCustomCommand(PlaybackContract.command(action), args) }
    }

    /** Runs [action] now, or as soon as the connection to the service is ready. */
    private fun withController(action: (MediaController) -> Unit) {
        val c = controller
        if (c != null) action(c) else pendingControllerActions += action
    }

    override fun close() {
        super.close()
        controller?.removeListener(playerListener)
        controller = null
        pendingControllerActions.clear()
        MediaController.releaseFuture(controllerFuture)
    }

    private companion object {
        const val TICK_MS = 1_000L
    }
}
