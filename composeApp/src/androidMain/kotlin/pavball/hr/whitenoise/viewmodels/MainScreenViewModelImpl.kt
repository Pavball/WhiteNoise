package pavball.hr.whitenoise.viewmodels

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal class MainScreenViewModelImpl(
    private val context: Context
) : MainScreenViewModel() {

    // --- Player handling ---
    private var exoPlayer: ExoPlayer? = null
    private var currentSound: String? = null
    private var isLoading = false
    private var isPlaying = false

    // --- Timer handling ---
    private var remainingTimeMs: Long = 0L
    private var totalTimeMs: Long = 0L
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

    // --- Playback methods ---
    override fun playSound(soundId: String) {
        runCommand {
            val resId = resourceMap[soundId] ?: return@runCommand
            isLoading = true
            emit(MainScreenViewState.PlayerState(isPlaying, isLoading, soundId))

            runOnMain {
                exoPlayer?.stop()
                exoPlayer?.release()

                val player = ExoPlayer.Builder(context).build().also {
                    val uri = "android.resource://${context.packageName}/$resId"
                    val mediaItem = MediaItem.fromUri(uri)
                    it.setMediaItem(mediaItem)
                    it.prepare()
                    it.play()
                }

                exoPlayer = player
            }

            isPlaying = true
            currentSound = soundId
            isLoading = false

            emit(MainScreenViewState.PlayerState(isPlaying, isLoading, currentSound))
        }
    }

    override fun pauseSound() {
        runCommand {
            runOnMain {
                exoPlayer?.pause()
            }
            isPlaying = false
            emit(MainScreenViewState.PlayerState(isPlaying, isLoading, currentSound))
        }
    }

    override fun stopSound() {
        runCommand {
            runOnMain {
                exoPlayer?.pause()
                exoPlayer?.seekTo(0)
            }

            isPlaying = false
            emit(MainScreenViewState.PlayerState(isPlaying, isLoading, currentSound))
        }
    }


    @OptIn(ExperimentalTime::class)
    override fun startTimer(
        minutes: Int,
        fadeOutEnabled: Boolean
    ) {
        cancelTimer()

        this.fadeOutEnabled = fadeOutEnabled
        totalTimeMs = minutes * 60 * 1000L
        remainingTimeMs = totalTimeMs
        fadeStarted = false
        endTime = Clock.System.now().plus(remainingTimeMs.milliseconds)

        timerJob = query {
            flow<MainScreenViewState> {
                while (remainingTimeMs > 0) {
                    delay(1.seconds)
                    val remaining = endTime?.let { it - Clock.System.now() } ?: break
                    remainingTimeMs = remaining.inWholeMilliseconds.coerceAtLeast(0)

                    if (fadeOutEnabled && !fadeStarted && remainingTimeMs <= 30_000L) {
                        fadeStarted = true
                        fadeOutVolume()
                    }

                    emit(MainScreenViewState.TimerRunning(remainingTimeMs, totalTimeMs, fadeStarted))
                }

                if (remainingTimeMs <= 0) {
                    stopSound()
                    emit(MainScreenViewState.TimerFinished)
                }
            }.flowOn(Dispatchers.Default)
        }
    }



    @OptIn(ExperimentalTime::class)
    override fun pauseTimer() {
        endTime = null
        runCommand {
            emit(MainScreenViewState.TimerPaused(remainingTimeMs, totalTimeMs))
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun resumeTimer(onFadeStart: () -> Unit, onTimerFinished: () -> Unit) {
        if (remainingTimeMs > 0) {
            endTime = Clock.System.now().plus(remainingTimeMs.milliseconds)
            query {
                flow {
                    while (remainingTimeMs > 0) {
                        delay(1.seconds)
                        val remaining = endTime?.let { it - Clock.System.now() } ?: break
                        remainingTimeMs = remaining.inWholeMilliseconds.coerceAtLeast(0)

                        if (fadeOutEnabled && !fadeStarted && remainingTimeMs <= 30_000L) {
                            fadeStarted = true
                            onFadeStart()
                        }

                        emit(MainScreenViewState.TimerRunning(remainingTimeMs, totalTimeMs, fadeStarted))
                    }

                    if (remainingTimeMs <= 0) {
                        onTimerFinished()
                        emit(MainScreenViewState.TimerFinished)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        remainingTimeMs = 0L
        fadeStarted = false
        endTime = null
        runCommand { emit(MainScreenViewState.Initial) }
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

