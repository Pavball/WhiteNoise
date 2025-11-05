package pavball.hr.whitenoise.viewmodels

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
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
            viewState.emit(MainScreenViewState.PlayerState(true, isLoading, currentSound))

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
            isPlaying = true
            currentSound = soundId
            isLoading = false

            viewState.emit(MainScreenViewState.PlayerState(isPlaying, isLoading, currentSound))
        }
    }

    override fun pauseSound() {
        exoPlayer?.pause()
        isPlaying = false
        runCommand {
            viewState.emit(MainScreenViewState.PlayerState(isPlaying, isLoading, currentSound))
        }
    }

    override fun stopSound() {
        exoPlayer?.pause()
        exoPlayer?.seekTo(0)
        isPlaying = false
        runCommand {
            viewState.emit(MainScreenViewState.PlayerState(isPlaying, isLoading, currentSound))
        }
    }

    // --- Timer logic ---
    override fun startTimer(
        minutes: Int,
        fadeOutEnabled: Boolean,
        onFadeStart: () -> Unit,
        onTimerFinished: () -> Unit
    ) {
        cancelTimer()

        this.fadeOutEnabled = fadeOutEnabled
        totalTimeMs = minutes * 60 * 1000L
        remainingTimeMs = totalTimeMs
        fadeStarted = false
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

    override fun pauseTimer() {
        endTime = null
        runCommand {
            viewState.emit(
                MainScreenViewState.TimerPaused(remainingTimeMs, totalTimeMs)
            )
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

    override fun cancelTimer() {
        remainingTimeMs = 0L
        fadeStarted = false
        endTime = null
        runCommand { viewState.emit(MainScreenViewState.Initial) }
    }

    override fun close() {
        super.close()
        exoPlayer?.release()
    }
}