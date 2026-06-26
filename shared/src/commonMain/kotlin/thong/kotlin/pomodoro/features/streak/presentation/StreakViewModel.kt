package thong.kotlin.pomodoro.features.streak.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import thong.kotlin.pomodoro.features.streak.data.getCurrentDate
import thong.kotlin.pomodoro.features.streak.domain.StreakCalculator
import thong.kotlin.pomodoro.features.streak.domain.StreakRepository
import thong.kotlin.pomodoro.features.streak.domain.model.DailyRecord

data class StreakUiState(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val heatMapData: List<DailyRecord> = emptyList(),
    val isLoading: Boolean = true,
    val pomodoroProgress: Float = 0f,  // 0..1 progress of current pomodoro
    val isInPomodoro: Boolean = false
)

enum class PetState {
    SLEEPING, IDLE, FOCUS_LVL1, FOCUS_LVL2, FOCUS_LVL3, FOCUS_END, BREAK, BREAK_END
}

class StreakViewModel(
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakUiState())
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    init {
        loadStreak()
    }

    fun loadStreak() {
        viewModelScope.launch {
            streakRepository.getHistoryFlow().collect { history ->
                val streakData = StreakCalculator.calculate(history, getCurrentDate())
                _uiState.update {
                    it.copy(
                        currentStreak = streakData.currentStreak,
                        longestStreak = streakData.longestStreak,
                        heatMapData = streakData.history,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updatePomodoroProgress(progress: Float, isActive: Boolean) {
        _uiState.update { it.copy(pomodoroProgress = progress, isInPomodoro = isActive) }
    }

    fun getPetState(): PetState {
        val state = _uiState.value
        if (!state.isInPomodoro) {
            return if (state.currentStreak == 0) PetState.SLEEPING else PetState.IDLE
        }
        return when {
            state.pomodoroProgress < 0.33f -> PetState.FOCUS_LVL1
            state.pomodoroProgress < 0.66f -> PetState.FOCUS_LVL2
            else -> PetState.FOCUS_LVL3
        }
    }
}
