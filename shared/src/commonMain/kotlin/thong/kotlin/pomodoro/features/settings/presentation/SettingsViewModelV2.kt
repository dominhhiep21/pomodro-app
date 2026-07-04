package thong.kotlin.pomodoro.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettingsV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2

data class SettingsUiState(
    val userSettings: UserSettingsV2 = UserSettingsV2(),
    val isLoading: Boolean = false
)

class SettingsViewModelV2(
    private val repository: UserAppStateRepositoryV2 = DependencyRegistry.userAppStateRepositoryV2
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        repository.getSettingsFlow()
            .onEach { settings ->
                _uiState.update { it.copy(userSettings = settings) }
            }
            .launchIn(viewModelScope)
    }

    fun updateWorkMinutes(minutes: Int) {
        val current = _uiState.value.userSettings
        repository.updatePersonalPomodoroTime(
            minutes,
            current.personalBreakMinutes,
            current.personalLongBreakMinutes
        )
    }

    fun updateBreakMinutes(minutes: Int) {
        val current = _uiState.value.userSettings
        repository.updatePersonalPomodoroTime(
            current.personalWorkMinutes,
            minutes,
            current.personalLongBreakMinutes
        )
    }

    fun updateLongBreakMinutes(minutes: Int) {
        val current = _uiState.value.userSettings
        repository.updatePersonalPomodoroTime(
            current.personalWorkMinutes,
            current.personalBreakMinutes,
            minutes
        )
    }

    fun updateDailyTarget(minutes: Int) = repository.updateDailyTarget(minutes)

    fun toggleNotification(enabled: Boolean) {
        repository.updateUserSettings { it.copy(isNotificationEnabled = enabled) }
    }

    fun toggleSound(enabled: Boolean) = repository.updateNotificationSound(enabled)

    fun toggleVibration(enabled: Boolean) = repository.updateVibration(enabled)

    fun toggleAutoStartBreak(enabled: Boolean) {
        val current = _uiState.value.userSettings
        repository.updateAutoStartSettings(enabled, current.autoStartWork)
    }

    fun toggleAutoStartWork(enabled: Boolean) {
        val current = _uiState.value.userSettings
        repository.updateAutoStartSettings(current.autoStartBreak, enabled)
    }

    fun updateFirstDayOfWeek(day: Int) = repository.updateFirstDayOfWeek(day)

    fun updateLanguage(lang: String) = repository.updateLanguage(lang)

    fun updateSelectedBackground(id: String?) = repository.updateSelectedBackground(id)

    fun updateSelectedMusic(id: String?) =
        repository.updateUserSettings { it.copy(personalLastSelectedMusicId = id) }

    fun toggleDarkMode(enabled: Boolean) = repository.updateDarkMode(enabled)

    fun resetToDefault() = repository.resetUserSettings()
}