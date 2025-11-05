package pavball.hr.whitenoise.ui.viewmodels

internal sealed class MainScreenViewState {

    data object Initial : MainScreenViewState()

    data class PlayerState(
        val isPlaying: Boolean,
        val isLoading: Boolean,
        val currentSound: String?
    ) : MainScreenViewState()

    data class TimerRunning(
        val remainingTime: Long,
        val totalTime: Long,
        val fadeStarted: Boolean
    ) : MainScreenViewState()

    data class TimerPaused(
        val remainingTime: Long,
        val totalTime: Long
    ) : MainScreenViewState()

    data object TimerFinished : MainScreenViewState()
}

internal abstract class MainScreenViewModel : BaseViewModel<MainScreenViewState>() {

    abstract fun playSound(soundId: String)
    abstract fun pauseSound()
    abstract fun stopSound()

    abstract fun startTimer(
        minutes: Int,
        fadeOutEnabled: Boolean
    )

    abstract fun pauseTimer()
    abstract fun resumeTimer(onFadeStart: () -> Unit, onTimerFinished: () -> Unit)
    abstract fun cancelTimer()
}

