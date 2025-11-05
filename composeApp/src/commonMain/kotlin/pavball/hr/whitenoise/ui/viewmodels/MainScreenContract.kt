package pavball.hr.whitenoise.ui.viewmodels

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal sealed class MainScreenViewState {

    /** Initial idle state */
    data object Initial : MainScreenViewState()

    /** Media player state */
    data class PlayerState(
        val isPlaying: Boolean,
        val isLoading: Boolean,
        val currentSound: String?
    ) : MainScreenViewState()

    /** Timer running */
    data class TimerRunning(
        val remainingTime: Long,
        val totalTime: Long,
        val fadeStarted: Boolean
    ) : MainScreenViewState()

    /** Timer paused */
    data class TimerPaused(
        val remainingTime: Long,
        val totalTime: Long
    ) : MainScreenViewState()

    /** Timer finished completely */
    data object TimerFinished : MainScreenViewState()
}

internal abstract class MainScreenViewModel : BaseViewModel<MainScreenViewState>() {

    abstract fun playSound(soundId: String)
    abstract fun pauseSound()
    abstract fun stopSound()

    abstract fun startTimer(
        minutes: Int,
        fadeOutEnabled: Boolean = true,
        onFadeStart: () -> Unit,
        onTimerFinished: () -> Unit
    )

    abstract fun pauseTimer()
    abstract fun resumeTimer(onFadeStart: () -> Unit, onTimerFinished: () -> Unit)
    abstract fun cancelTimer()
}

