package pavball.hr.whitenoise.viewmodels

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal class MainScreenViewModelImpl(
    private val context: Context
) : MainScreenViewModel() {

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
        "forest" to pavball.hr.whitenoise.R.raw.winter_forest
    )

    // --- Playback ---

    override fun playSound(soundId: String) {
        runCommand {
            val resId = resourceMap[soundId] ?: return@runCommand
            updateState { copy(isLoading = true, currentSound = soundId) }

            runOnMain {
                val sameSound = currentSound == soundId && exoPlayer != null
                if (sameSound) {
                    exoPlayer?.playWhenReady = true
                    exoPlayer?.play()
                } else {
                    exoPlayer?.release()
                    val player = ExoPlayer.Builder(context).build().also {
                        val uri = "android.resource://${context.packageName}/$resId"
                        val mediaItem = MediaItem.fromUri(uri)
                        it.setMediaItem(mediaItem)
                        it.prepare()
                        it.play()
                    }
                    currentSound = soundId
                    exoPlayer = player
                }
            }

            updateState { copy(isPlaying = true, isLoading = false) }
        }
    }

    override fun pauseSound() {
        runCommand {
            runOnMain { exoPlayer?.pause() }
            updateState { copy(isPlaying = false) }
        }
    }

    override fun updateSelectedSoundKey(selectedSoundKey: String){
        runCommand {
            updateState { copy(selectedSoundKey = selectedSoundKey) }
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
    }


    @OptIn(ExperimentalTime::class)
    override fun pauseTimer() {
        // Stop counting but keep remainingTime
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
            fadeOutEnabled = fadeOutEnabled // keep last used flag

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
                    isPlaying = false, // optional if your state has this
                    remainingTime = null,
                    totalTime = null,
                    fadeStarted = false,
                    timerFinished = false
                )
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
}

private suspend fun runOnMain(block: () -> Unit) {
    withContext(Dispatchers.Main) { block() }
}
