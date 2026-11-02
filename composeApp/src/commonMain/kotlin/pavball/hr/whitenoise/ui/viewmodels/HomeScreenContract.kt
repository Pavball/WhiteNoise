package pavball.hr.whitenoise.ui.viewmodels

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import pavball.hr.whitenoise.domain.model.CustomSound
import pavball.hr.whitenoise.domain.model.SoundMix


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

    // --- Mixer: the selected sound plays at leadVolume, layers play on top of it ---
    val leadVolume: Float = 1f,
    val layers: Map<String, Float> = emptyMap(),
    val savedMixes: List<SoundMix> = emptyList(),

    // --- Sleep timer (null when no timer is active) ---
    val remainingTime: Long? = null,
    val totalTime: Long? = null,
    val isTimerRunning: Boolean = false,
    val fadeStarted: Boolean = false,

    // --- Persistent user settings ---
    val fadeEnabled: Boolean = true,
    val fadeDuration: Int = 30,  // seconds
    val timerSelectedMinutes: Int = 0, // 0 = no timer
    val themeMode: String = ThemeMode.SYSTEM.name, // "light", "dark", "system"

    // --- First-run tutorials (null until the settings have loaded, so nothing flashes) ---
    val onboardingSeen: Boolean? = null,
    val whiteNoiseTipsSeen: Boolean? = null
) {
    /** Every playable sound id in playback order: built-in first, then the user's own. */
    val playlistIds: List<String>
        get() = sounds.map { it.second } + customSounds.map { it.id }

    val selectedCustomSound: CustomSound?
        get() = customSounds.firstOrNull { it.id == selectedSoundKey }

    val selectedSoundLabel: String?
        get() = labelFor(selectedSoundKey)

    /** Layers that actually play: the selected sound is never layered on top of itself. */
    val activeLayers: Map<String, Float>
        get() = layers.filterKeys { it != selectedSoundKey }

    fun labelFor(soundId: String): String? =
        sounds.firstOrNull { it.second == soundId }?.first
            ?: customSounds.firstOrNull { it.id == soundId }?.displayName

    companion object {
        /** Sound keys that are generated on the device instead of bundled. */
        val noiseSoundKeys = listOf("white_noise", "pink_noise", "brown_noise")

        val defaultSounds = listOf(
            "Rain" to "rain",
            "Ocean Waves" to "ocean",
            "Forest Ambience" to "forest",
            "Thunder Rain" to "thunder",
            "White Noise" to "white_noise",
            "Pink Noise" to "pink_noise",
            "Brown Noise" to "brown_noise"
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

    /**
     * Loudness of [SPECTRUM_BANDS] frequency bands (low to high, each 0..1) of what is
     * playing right now, updated about 60 times a second while collected. Empty when
     * the platform can't analyse its audio.
     */
    abstract val spectrum: Flow<FloatArray>

    // playback
    abstract fun playSound(soundId: String)
    abstract fun togglePlayPause()
    abstract fun skipToNext()
    abstract fun skipToPrevious()

    // mixer
    abstract fun setLeadVolume(volume: Float)
    abstract fun setLayerVolume(soundId: String, volume: Float) // 0 removes the layer
    abstract fun clearLayers()
    abstract fun applyMix(mix: SoundMix)
    abstract fun saveCurrentMix(name: String)
    abstract fun deleteMix(id: Long)

    // sleep timer
    abstract fun updateSelectedTimer(minutes: Int)
    abstract fun extendTimer(minutes: Int)
    abstract fun cancelTimer()

    // settings
    abstract fun saveThemeModeToUserPrefs(themeMode: String)
    abstract fun saveFadeOutTimeToUserPrefs(fadeTime: Int)
    abstract fun updateFadeEnabled(enabled: Boolean)

    // first-run tutorials
    abstract fun completeOnboarding()
    abstract fun completeWhiteNoiseTips()
    abstract fun replayTutorials()

    // custom sound management
    abstract fun addUserSound(displayName: String, uri: String)
    abstract fun renameCustomSound(id: String, newName: String)
    abstract fun removeUserSound(id: String)
    abstract fun clearPendingRename()
    abstract fun updateCustomSoundColor(id: String, colorId: String)

    companion object {
        const val SPECTRUM_BANDS = 16
    }
}
