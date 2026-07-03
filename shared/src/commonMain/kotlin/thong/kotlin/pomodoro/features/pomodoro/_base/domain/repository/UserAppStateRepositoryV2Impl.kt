package thong.kotlin.pomodoro.features.pomodoro._base.domain.repository

import kotlinx.coroutines.flow.Flow
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettingsV2

class UserAppStateRepositoryImplV2(
    private val localSettingsDataSource: LocalSettingsDataSourceV2
) : UserAppStateRepositoryV2 {

    override fun getUserSettings(): UserSettingsV2 = localSettingsDataSource.getUserSettings()

    override fun getSettingsFlow(): Flow<UserSettingsV2> = localSettingsDataSource.getSettingsFlow()

    override fun saveUserSettings(settings: UserSettingsV2) = localSettingsDataSource.saveUserSettings(settings)

    override fun updateUserSettings(transform: (UserSettingsV2) -> UserSettingsV2) {
        val currentSettings = getUserSettings()
        val newSettings = transform(currentSettings)
        saveUserSettings(newSettings)
    }

    override fun markOnboardingCompleted() {
        updateUserSettings { currentSettings ->
            currentSettings.copy(
                hasCompletedOnboarding = true
            )
        }
    }

    override fun updatePersonalPomodoroTime(
        workMinutes: Int,
        breakMinutes: Int,
        longBreakMinutes: Int
    ) {
        updateUserSettings { currentSettings ->
            currentSettings.copy(
                personalWorkMinutes = workMinutes,
                personalBreakMinutes = breakMinutes,
                personalLongBreakMinutes = longBreakMinutes
            )
        }
    }

    override fun updateAutoStartSettings(autoStartBreak: Boolean, autoStartWork: Boolean) {
        updateUserSettings { currentSettings ->
            currentSettings.copy(
                autoStartBreak = autoStartBreak,
                autoStartWork = autoStartWork
            )
        }
    }

    override fun updateSelectedBackground(backgroundId: String?) {
        updateUserSettings { currentSettings ->
            currentSettings.copy(
                personalSelectedBackgroundId = backgroundId
            )
        }
    }

    override fun resetUserSettings() = localSettingsDataSource.clearUserSettings()

}