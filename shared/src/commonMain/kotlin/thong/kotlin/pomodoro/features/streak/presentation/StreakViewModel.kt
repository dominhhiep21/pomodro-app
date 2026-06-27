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
    val isLoading: Boolean = true
)

class StreakViewModel(
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakUiState())
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    init {
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
}
