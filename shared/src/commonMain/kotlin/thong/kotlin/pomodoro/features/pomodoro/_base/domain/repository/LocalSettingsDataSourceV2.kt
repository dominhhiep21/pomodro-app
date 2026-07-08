package thong.kotlin.pomodoro.features.pomodoro._base.domain.repository

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettingsV2

class LocalSettingsDataSourceV2(
    private val settings: Settings
) {
    private val _settingsFlow = MutableStateFlow(getUserSettings())

    companion object {
        private const val KEY_PERSONAL_WORK_MINUTES = "personal_work_minutes"
        private const val KEY_PERSONAL_BREAK_MINUTES = "personal_break_minutes"
        private const val KEY_PERSONAL_LONG_BREAK_MINUTES = "personal_long_break_minutes"
        private const val KEY_AUTO_START_BREAK = "auto_start_break"
        private const val KEY_AUTO_START_WORK = "auto_start_work"
        private const val KEY_PERSONAL_SELECTED_BACKGROUND_ID = "personal_selected_background_id"
        private const val KEY_PERSONAL_SELECTED_MUSIC_ID = "personal_selected_music_id"
        private const val KEY_HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"
        private const val KEY_IS_NOTIFICATION_ENABLED = "is_notification_enabled"
        private const val KEY_DAILY_TARGET_MINUTES = "daily_target_minutes"
        private const val KEY_IS_SOUND_ENABLED = "is_sound_enabled"
        private const val KEY_IS_VIBRATION_ENABLED = "is_vibration_enabled"
        private const val KEY_FIRST_DAY_OF_WEEK = "first_day_of_week"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
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
            personalLastSelectedMusicId = settings.getStringOrNull(
                key = KEY_PERSONAL_SELECTED_MUSIC_ID
            ),
            hasCompletedOnboarding = settings.getBoolean(
                key = KEY_HAS_COMPLETED_ONBOARDING,
                defaultValue = false
            ),
            isNotificationEnabled = settings.getBoolean(
                key = KEY_IS_NOTIFICATION_ENABLED,
                defaultValue = true
            ),
            dailyTargetMinutes = settings.getInt(
                key = KEY_DAILY_TARGET_MINUTES,
                defaultValue = 120
            ),
            isSoundEnabled = settings.getBoolean(
                key = KEY_IS_SOUND_ENABLED,
                defaultValue = true
            ),
            isVibrationEnabled = settings.getBoolean(
                key = KEY_IS_VIBRATION_ENABLED,
                defaultValue = true
            ),
            firstDayOfWeek = settings.getInt(
                key = KEY_FIRST_DAY_OF_WEEK,
                defaultValue = 1
            ),
            language = settings.getString(
                key = KEY_LANGUAGE,
                defaultValue = "vi"
            ),
            isDarkMode = settings.getBoolean(
                key = KEY_IS_DARK_MODE,
                defaultValue = true
            )
        )
    }

    fun getSettingsFlow(): Flow<UserSettingsV2> = _settingsFlow.asStateFlow()

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

        if (userSettings.personalLastSelectedMusicId == null) {
            settings.remove(KEY_PERSONAL_SELECTED_MUSIC_ID)
        } else {
            settings.putString(
                key = KEY_PERSONAL_SELECTED_MUSIC_ID,
                value = userSettings.personalLastSelectedMusicId
            )
        }

        settings.putBoolean(
            key = KEY_HAS_COMPLETED_ONBOARDING,
            value = userSettings.hasCompletedOnboarding
        )

        settings.putBoolean(
            key = KEY_IS_NOTIFICATION_ENABLED,
            value = userSettings.isNotificationEnabled
        )

        settings.putInt(
            key = KEY_DAILY_TARGET_MINUTES,
            value = userSettings.dailyTargetMinutes
        )

        settings.putBoolean(
            key = KEY_IS_SOUND_ENABLED,
            value = userSettings.isSoundEnabled
        )

        settings.putBoolean(
            key = KEY_IS_VIBRATION_ENABLED,
            value = userSettings.isVibrationEnabled
        )

        settings.putInt(
            key = KEY_FIRST_DAY_OF_WEEK,
            value = userSettings.firstDayOfWeek
        )

        settings.putString(
            key = KEY_LANGUAGE,
            value = userSettings.language
        )

        settings.putBoolean(
            key = KEY_IS_DARK_MODE,
            value = userSettings.isDarkMode
        )
        
        _settingsFlow.value = userSettings
    }

    fun clearUserSettings() {
        settings.remove(KEY_PERSONAL_WORK_MINUTES)
        settings.remove(KEY_PERSONAL_BREAK_MINUTES)
        settings.remove(KEY_PERSONAL_LONG_BREAK_MINUTES)
        settings.remove(KEY_AUTO_START_BREAK)
        settings.remove(KEY_AUTO_START_WORK)
        settings.remove(KEY_PERSONAL_SELECTED_BACKGROUND_ID)
        settings.remove(KEY_HAS_COMPLETED_ONBOARDING)
        settings.remove(KEY_IS_NOTIFICATION_ENABLED)
        settings.remove(KEY_DAILY_TARGET_MINUTES)
        settings.remove(KEY_IS_SOUND_ENABLED)
        settings.remove(KEY_IS_VIBRATION_ENABLED)
        settings.remove(KEY_FIRST_DAY_OF_WEEK)
        settings.remove(KEY_LANGUAGE)
        settings.remove(KEY_IS_DARK_MODE)
        
        _settingsFlow.value = getUserSettings()
    }
}