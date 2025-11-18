package pavball.hr.whitenoise.ui.components

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
private val Context.dataStore by preferencesDataStore("user_settings")
data class UserSettings(
    val lastSound: String? = null,
    val timerMinutes: Int = 0,
    val fadeEnabled: Boolean = true,
    val fadeDuration: Int = 30,
    val themeMode: String = "system"
)
class SettingsDataStore(private val context: Context) {
    companion object {
        private val LAST_SOUND_KEY = stringPreferencesKey("last_sound")
        private val TIMER_MINUTES_KEY = intPreferencesKey("timer_minutes")
        private val FADE_ENABLED_KEY = booleanPreferencesKey("fade_enabled")
        private val FADE_DURATION_KEY = intPreferencesKey("fade_duration")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            lastSound = prefs[LAST_SOUND_KEY],
            timerMinutes = prefs[TIMER_MINUTES_KEY] ?: 0,
            fadeEnabled = prefs[FADE_ENABLED_KEY] ?: true,
            fadeDuration = prefs[FADE_DURATION_KEY] ?: 30,
            themeMode = prefs[THEME_MODE_KEY] ?: "system"
        )
    }
    suspend fun saveSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[LAST_SOUND_KEY] = settings.lastSound ?: ""
            prefs[TIMER_MINUTES_KEY] = settings.timerMinutes
            prefs[FADE_ENABLED_KEY] = settings.fadeEnabled
            prefs[FADE_DURATION_KEY] = settings.fadeDuration
            prefs[THEME_MODE_KEY] = settings.themeMode
        }
    }
}
