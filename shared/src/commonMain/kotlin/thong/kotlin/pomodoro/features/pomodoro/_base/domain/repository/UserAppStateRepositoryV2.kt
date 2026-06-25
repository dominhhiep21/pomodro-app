package thong.kotlin.pomodoro.features.pomodoro._base.domain.repository

import kotlinx.coroutines.flow.Flow
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettingsV2

interface UserAppStateRepositoryV2 {

    fun getUserSettings(): UserSettingsV2

    fun getSettingsFlow(): Flow<UserSettingsV2>

    fun saveUserSettings(settings: UserSettingsV2)

    fun updateUserSettings(
        transform: (UserSettingsV2) -> UserSettingsV2
    )

    fun markOnboardingCompleted()

    fun updatePersonalPomodoroTime(
        workMinutes: Int,
        breakMinutes: Int,
        longBreakMinutes: Int
    )

    fun updateAutoStartSettings(
        autoStartBreak: Boolean,
        autoStartWork: Boolean
    )

    fun updateSelectedBackground(backgroundId: String?)

    fun resetUserSettings()
}