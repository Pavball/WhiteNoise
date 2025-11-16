package pavball.hr.whitenoise.ui.components

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pavball.hr.whitenoise.domain.model.CustomSound

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
        private val USER_SOUNDS_KEY = stringPreferencesKey("user_sounds")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            lastSound = prefs[LAST_SOUND_KEY]?.takeIf { it.isNotBlank() },
            timerMinutes = prefs[TIMER_MINUTES_KEY] ?: 0,
            fadeEnabled = prefs[FADE_ENABLED_KEY] ?: true,
            fadeDuration = prefs[FADE_DURATION_KEY] ?: 30,
            themeMode = prefs[THEME_MODE_KEY] ?: "system"
        )
    }

    suspend fun saveSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            if (settings.lastSound.isNullOrBlank()) {
                prefs.remove(LAST_SOUND_KEY)
            } else {
                prefs[LAST_SOUND_KEY] = settings.lastSound
            }

            prefs[TIMER_MINUTES_KEY] = settings.timerMinutes
            prefs[FADE_ENABLED_KEY] = settings.fadeEnabled
            prefs[FADE_DURATION_KEY] = settings.fadeDuration
            prefs[THEME_MODE_KEY] = settings.themeMode
        }
    }

    // persisted list of custom sounds (JSON)
    val userSoundsFlow: Flow<List<CustomSound>> = context.dataStore.data.map { prefs ->
        prefs[USER_SOUNDS_KEY]?.let { json ->
            try {
                Json.decodeFromString<List<CustomSound>>(json)
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    suspend fun saveUserSounds(list: List<CustomSound>) {
        val json = Json.encodeToString(list)
        context.dataStore.edit { prefs ->
            prefs[USER_SOUNDS_KEY] = json
        }
    }
}
