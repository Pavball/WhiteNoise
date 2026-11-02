package pavball.hr.whitenoise.ui.components

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pavball.hr.whitenoise.domain.model.SoundMix
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState.Companion.ThemeMode

private val Context.dataStore by preferencesDataStore("user_settings")
data class UserSettings(
    val lastSound: String? = null,
    val timerMinutes: Int = 0,
    val fadeEnabled: Boolean = true,
    val fadeDuration: Int = 30,
    val themeMode: String = ThemeMode.SYSTEM.name,
    val mixLeadVolume: Float = 1f,
    val mixLayers: Map<String, Float> = emptyMap(),
    val onboardingSeen: Boolean = false,
    val whiteNoiseTipsSeen: Boolean = false
)

/**
 * Each setting is written on its own so the UI and the playback service
 * (which records the last played sound and the mix) never overwrite each other's values.
 */
class SettingsDataStore(private val context: Context) {
    companion object {
        private val LAST_SOUND_KEY = stringPreferencesKey("last_sound")
        private val TIMER_MINUTES_KEY = intPreferencesKey("timer_minutes")
        private val FADE_ENABLED_KEY = booleanPreferencesKey("fade_enabled")
        private val FADE_DURATION_KEY = intPreferencesKey("fade_duration")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val MIX_LEAD_VOLUME_KEY = floatPreferencesKey("mix_lead_volume")
        private val MIX_LAYERS_KEY = stringPreferencesKey("mix_layers")
        private val ONBOARDING_SEEN_KEY = booleanPreferencesKey("onboarding_seen")
        private val WHITE_NOISE_TIPS_SEEN_KEY = booleanPreferencesKey("white_noise_tips_seen")
    }
    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            lastSound = prefs[LAST_SOUND_KEY]?.takeIf { it.isNotBlank() },
            timerMinutes = prefs[TIMER_MINUTES_KEY] ?: 0,
            fadeEnabled = prefs[FADE_ENABLED_KEY] ?: true,
            fadeDuration = prefs[FADE_DURATION_KEY] ?: 30,
            themeMode = prefs[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name,
            mixLeadVolume = prefs[MIX_LEAD_VOLUME_KEY] ?: 1f,
            mixLayers = prefs[MIX_LAYERS_KEY]?.let(SoundMix::decodeLayers).orEmpty(),
            onboardingSeen = prefs[ONBOARDING_SEEN_KEY] ?: false,
            whiteNoiseTipsSeen = prefs[WHITE_NOISE_TIPS_SEEN_KEY] ?: false
        )
    }

    suspend fun saveLastSound(soundId: String) = save(LAST_SOUND_KEY, soundId)
    suspend fun saveTimerMinutes(minutes: Int) = save(TIMER_MINUTES_KEY, minutes)
    suspend fun saveFadeEnabled(enabled: Boolean) = save(FADE_ENABLED_KEY, enabled)
    suspend fun saveFadeDuration(seconds: Int) = save(FADE_DURATION_KEY, seconds)
    suspend fun saveThemeMode(themeMode: String) = save(THEME_MODE_KEY, themeMode)
    suspend fun saveOnboardingSeen(seen: Boolean) = save(ONBOARDING_SEEN_KEY, seen)
    suspend fun saveWhiteNoiseTipsSeen(seen: Boolean) = save(WHITE_NOISE_TIPS_SEEN_KEY, seen)

    suspend fun saveMix(leadVolume: Float, layers: Map<String, Float>) {
        context.dataStore.edit { prefs ->
            prefs[MIX_LEAD_VOLUME_KEY] = leadVolume
            prefs[MIX_LAYERS_KEY] = SoundMix.encodeLayers(layers)
        }
    }

    private suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { prefs -> prefs[key] = value }
    }
}
