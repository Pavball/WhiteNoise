package pavball.hr.whitenoise.ui.viewmodels


internal data class MainScreenViewState(
    val sounds: List<Pair<String, String>> = defaultSounds,
    val timerOptions: List<Int> = defaultTimerOptions,
    val selectedSoundKey: String = "rain",

    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSound: String? = null,

    val remainingTime: Long? = null,
    val totalTime: Long? = null,
    val fadeStarted: Boolean = false,
    val timerFinished: Boolean = false,

    // --- Persistent user settings ---
    val fadeEnabled: Boolean = true,
    val fadeDuration: Int = 30,  // seconds
    val timerSelectedMinutes: Int = 0,
    val themeMode: String = "system" // "light", "dark", "system"
) {
    companion object {
        val defaultSounds = listOf(
            "Rain" to "rain",
            "Ocean Waves" to "ocean",
            "Forest Ambience" to "forest",
            "Thunder Rain" to "thunder"
        )

        val defaultTimerOptions = listOf(5, 10, 15)
    }
}


internal abstract class MainScreenViewModel : BaseViewModel<MainScreenViewState>(MainScreenViewState()) {

    abstract fun playSound(soundId: String)
    abstract fun pauseSound()
    abstract fun stopSound()

    abstract fun startTimer(minutes: Int, fadeOutEnabled: Boolean)
    abstract fun pauseTimer()
    abstract fun resumeTimer(onFadeStart: () -> Unit, onTimerFinished: () -> Unit)
    abstract fun cancelTimer()
    abstract fun updateSelectedSoundKey(selectedSoundKey: String)
    abstract fun saveThemeModeToUserPrefs(themeMode: String)
    abstract fun updateSelectedTimer(minutes: Int)

}

