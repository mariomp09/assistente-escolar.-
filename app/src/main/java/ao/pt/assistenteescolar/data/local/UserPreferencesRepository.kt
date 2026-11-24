package ao.pt.assistenteescolar.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

data class UserSettings(
    val studentName: String = "",
    val country: CountryCurriculum = CountryCurriculum.ANGOLA,
    val grade: String = "10.ª Classe",
    val shift: SchoolShift = SchoolShift.MORNING,
    val isDarkTheme: Boolean = false,
    val onboardingDone: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {
    private object Keys {
        val STUDENT_NAME = stringPreferencesKey("student_name")
        val COUNTRY = stringPreferencesKey("country_code")
        val GRADE = stringPreferencesKey("grade_label")
        val SHIFT = stringPreferencesKey("school_shift")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val ONBOARDING = booleanPreferencesKey("onboarding_done")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        val code = prefs[Keys.COUNTRY] ?: "AO"
        val country = if (code == "PT") CountryCurriculum.PORTUGAL else CountryCurriculum.ANGOLA
        val defaultGrade = if (country == CountryCurriculum.ANGOLA) "10.ª Classe" else "10.º Ano"
        val shift = if (prefs[Keys.SHIFT] == "AFTERNOON") SchoolShift.AFTERNOON else SchoolShift.MORNING
        UserSettings(
            studentName = prefs[Keys.STUDENT_NAME] ?: "",
            country = country,
            grade = prefs[Keys.GRADE] ?: defaultGrade,
            shift = shift,
            isDarkTheme = prefs[Keys.DARK_THEME] ?: false,
            onboardingDone = prefs[Keys.ONBOARDING] ?: false
        )
    }

    suspend fun saveSettings(
        studentName: String,
        country: CountryCurriculum,
        grade: String,
        shift: SchoolShift,
        isDarkTheme: Boolean,
        onboardingDone: Boolean = true
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.STUDENT_NAME] = studentName
            prefs[Keys.COUNTRY] = country.flagCode
            prefs[Keys.GRADE] = grade
            prefs[Keys.SHIFT] = shift.name
            prefs[Keys.DARK_THEME] = isDarkTheme
            prefs[Keys.ONBOARDING] = onboardingDone
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = isDark
        }
    }
}
