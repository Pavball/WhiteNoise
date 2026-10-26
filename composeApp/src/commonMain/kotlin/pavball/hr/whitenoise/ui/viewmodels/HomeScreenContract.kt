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
    val playbackError: Boolean = false,

    // --- Sleep timer (null when no timer is active) ---
    val remainingTime: Long? = null,
    val totalTime: Long? = null,
    val isTimerRunning: Boolean = false,
    val fadeStarted: Boolean = false,

    // --- Persistent user settings ---
    val fadeEnabled: Boolean = true,
    val fadeDuration: Int = 30,  // seconds
    val timerSelectedMinutes: Int = 0, // 0 = no timer
    val themeMode: String = ThemeMode.SYSTEM.name // "light", "dark", "system"
) {
    /** Every playable sound id in playback order: built-in first, then the user's own. */
    val playlistIds: List<String>
        get() = sounds.map { it.second } + customSounds.map { it.id }

    val selectedCustomSound: CustomSound?
        get() = customSounds.firstOrNull { it.id == selectedSoundKey }

    val selectedSoundLabel: String?
        get() = sounds.firstOrNull { it.second == selectedSoundKey }?.first
            ?: selectedCustomSound?.displayName

    companion object {
        val defaultSounds = listOf(
            "Rain" to "rain",
            "Ocean Waves" to "ocean",
            "Forest Ambience" to "forest",
            "Thunder Rain" to "thunder"
        )

        val defaultCustomSounds = emptyList<CustomSound>()

        // 0 means "no timer, play until stopped"
        val defaultTimerOptions = listOf(0, 5, 10, 15, 30, 45, 60, 90)

        val defaultFadeOutOptions = listOf(5, 10, 15, 20, 25, 30)

        val defaultThemeOptions = listOf("SYSTEM", "LIGHT", "DARK")

        enum class ThemeMode {
            DARK, LIGHT, SYSTEM
        }

    }
}

internal abstract class MainScreenViewModel : BaseViewModel<MainScreenViewState>(MainScreenViewState()) {
    abstract val pendingRename: StateFlow<CustomSound?> // used by UI to open rename dialog immediately

    // playback
    abstract fun playSound(soundId: String)
    abstract fun togglePlayPause()
    abstract fun skipToNext()
    abstract fun skipToPrevious()

    // sleep timer
    abstract fun updateSelectedTimer(minutes: Int)
    abstract fun extendTimer(minutes: Int)
    abstract fun cancelTimer()

    // settings
    abstract fun saveThemeModeToUserPrefs(themeMode: String)
    abstract fun saveFadeOutTimeToUserPrefs(fadeTime: Int)
    abstract fun updateFadeEnabled(enabled: Boolean)

    // custom sound management
    abstract fun addUserSound(displayName: String, uri: String)
    abstract fun renameCustomSound(id: String, newName: String)
    abstract fun removeUserSound(id: String)
    abstract fun clearPendingRename()
    abstract fun updateCustomSoundColor(id: String, colorId: String)
}

