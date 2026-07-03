package thong.kotlin.pomodoro.features.pomodoro._base.domain.repository

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettingsV2

class LocalSettingsDataSourceV2(
    private val settings: Settings
) {
    companion object {
        private const val KEY_PERSONAL_WORK_MINUTES = "personal_work_minutes"
        private const val KEY_PERSONAL_BREAK_MINUTES = "personal_break_minutes"
        private const val KEY_PERSONAL_LONG_BREAK_MINUTES = "personal_long_break_minutes"
        private const val KEY_AUTO_START_BREAK = "auto_start_break"
        private const val KEY_AUTO_START_WORK = "auto_start_work"
        private const val KEY_PERSONAL_SELECTED_BACKGROUND_ID = "personal_selected_background_id"
        private const val KEY_HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"
    }

    fun getUserSettings(): UserSettingsV2 {
        return UserSettingsV2(
            personalWorkMinutes = settings.getInt(
                key = KEY_PERSONAL_WORK_MINUTES,
                defaultValue = 25
            ),
            personalBreakMinutes = settings.getInt(
                key = KEY_PERSONAL_BREAK_MINUTES,
                defaultValue = 5
            ),
            personalLongBreakMinutes = settings.getInt(
                key = KEY_PERSONAL_LONG_BREAK_MINUTES,
                defaultValue = 15
            ),
            autoStartBreak = settings.getBoolean(
                key = KEY_AUTO_START_BREAK,
                defaultValue = false
            ),
            autoStartWork = settings.getBoolean(
                key = KEY_AUTO_START_WORK,
                defaultValue = false
            ),
            personalSelectedBackgroundId = settings.getStringOrNull(
                key = KEY_PERSONAL_SELECTED_BACKGROUND_ID
            ),
            hasCompletedOnboarding = settings.getBoolean(
                key = KEY_HAS_COMPLETED_ONBOARDING,
                defaultValue = false
            )
        )
    }

    fun getSettingsFlow(): Flow<UserSettingsV2> {
        return flow {
            emit(getUserSettings())
        }
    }

    fun saveUserSettings(userSettings: UserSettingsV2) {
        settings.putInt(
            key = KEY_PERSONAL_WORK_MINUTES,
            value = userSettings.personalWorkMinutes
        )

        settings.putInt(
            key = KEY_PERSONAL_BREAK_MINUTES,
            value = userSettings.personalBreakMinutes
        )

        settings.putInt(
            key = KEY_PERSONAL_LONG_BREAK_MINUTES,
            value = userSettings.personalLongBreakMinutes
        )

        settings.putBoolean(
            key = KEY_AUTO_START_BREAK,
            value = userSettings.autoStartBreak
        )

        settings.putBoolean(
            key = KEY_AUTO_START_WORK,
            value = userSettings.autoStartWork
        )

        if (userSettings.personalSelectedBackgroundId == null) {
            settings.remove(KEY_PERSONAL_SELECTED_BACKGROUND_ID)
        } else {
            settings.putString(
                key = KEY_PERSONAL_SELECTED_BACKGROUND_ID,
                value = userSettings.personalSelectedBackgroundId
            )
        }

        settings.putBoolean(
            key = KEY_HAS_COMPLETED_ONBOARDING,
            value = userSettings.hasCompletedOnboarding
        )
    }

    fun clearUserSettings() {
        settings.remove(KEY_PERSONAL_WORK_MINUTES)
        settings.remove(KEY_PERSONAL_BREAK_MINUTES)
        settings.remove(KEY_PERSONAL_LONG_BREAK_MINUTES)
        settings.remove(KEY_AUTO_START_BREAK)
        settings.remove(KEY_AUTO_START_WORK)
        settings.remove(KEY_PERSONAL_SELECTED_BACKGROUND_ID)
        settings.remove(KEY_HAS_COMPLETED_ONBOARDING)
    }
}