package pavball.hr.whitenoise.ui.viewmodels

import kotlinx.coroutines.flow.StateFlow
import pavball.hr.whitenoise.domain.model.CustomSound


data class MainScreenViewState(
    val sounds: List<Pair<String, String>> = defaultSounds,
    val customSounds: List<CustomSound> = defaultCustomSounds,
    val timerOptions: List<Int> = defaultTimerOptions,
    val fadeOptions: List<Int> = defaultFadeOutOptions,
    val themeOptions: List<String> = defaultThemeOptions,
    val selectedSoundKey: String = "rain",

    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val isCleared: Boolean = false,
    val currentSound: String? = null,

    val remainingTime: Long? = null,
    val totalTime: Long? = null,
    val fadeStarted: Boolean = false,
    val timerFinished: Boolean = false,


    // --- Persistent user settings ---
    val fadeEnabled: Boolean = true,
    val fadeDuration: Int = 30,  // seconds
    val timerSelectedMinutes: Int = 0,
    val themeMode: String = ThemeMode.SYSTEM.name // "light", "dark", "system"
) {
    companion object {
        val defaultSounds = listOf(
            "Rain" to "rain",
            "Ocean Waves" to "ocean",
            "Forest Ambience" to "forest",
            "Thunder Rain" to "thunder"
        )

        val defaultCustomSounds = emptyList<CustomSound>()

        val defaultTimerOptions = listOf(5, 10, 15, 20, 25, 30)

        val defaultFadeOutOptions = listOf(5, 10, 15, 20, 25, 30)

        val defaultThemeOptions = listOf("SYSTEM", "LIGHT", "DARK")

        enum class ThemeMode {
            DARK, LIGHT, SYSTEM
        }

    }
}

internal abstract class MainScreenViewModel : BaseViewModel<MainScreenViewState>(MainScreenViewState()) {
    abstract val pendingRename: StateFlow<CustomSound?> // used by UI to open rename dialog immediately

    abstract fun playSound(soundId: String)
    abstract fun pauseSound()
    abstract fun stopSound()

    abstract fun startTimer(minutes: Int, fadeOutEnabled: Boolean)
    abstract fun pauseTimer()
    abstract fun resumeTimer(onFadeStart: () -> Unit, onTimerFinished: () -> Unit)
    abstract fun cancelTimer()
    abstract fun updateSelectedSoundKey(selectedSoundKey: String)
    abstract fun saveThemeModeToUserPrefs(themeMode: String)
    abstract fun saveFadeOutTimeToUserPrefs(fadeTime: Int)
    abstract fun updateSelectedTimer(minutes: Int)

    // custom sound management
    abstract fun addUserSound(displayName: String, uri: String, colorId: String)
    abstract fun renameCustomSound(id: String, newName: String)
    abstract fun removeUserSound(id: String)
    abstract fun clearPendingRename()
    abstract fun updateFadeDuration(newValue: Int)
    abstract fun updateFadeEnabled(enabled: Boolean)
    abstract fun updateCustomSoundColor(id: String, colorId: String)

    open val currentSoundColorId: String?
        get() = null
}

