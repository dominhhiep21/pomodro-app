package thong.kotlin.pomodoro.features.startup.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.utils.formatSeconds
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.pomodoro._base.domain.StatCardType
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2
import thong.kotlin.pomodoro.features.session.data.LearningSessionManager
import thong.kotlin.pomodoro.features.startup.domain.HomeUiDomain

data class HomeUiState(
    val isFirstTime: Boolean = true,
    val homeUiStats: HomeUiDomain = HomeUiDomain()
)

data class TrendUiState(
    val text: String,
    val color: Color
)

fun buildTrendUiState(
    trend: String,
    type: StatCardType,
    isCompact: Boolean = false
): TrendUiState? {
    val trendValue = trend.toIntOrNull() ?: return null

    return when (type) {
        StatCardType.TOTAL_FOCUS_TIME -> {
            val isDecrease = trendValue < 0
            val absValue = kotlin.math.abs(trendValue)

            TrendUiState(
                text = if (isDecrease) {
                    "↓ -${absValue.formatSeconds()}${if (isCompact) "" else " so với hôm qua"}"
                } else {
                    "↑ +${absValue.formatSeconds()}${if (isCompact) "" else " so với hôm qua"}"
                },
                color = if (isDecrease) {
                    AuraColors.DecreaseMode
                } else {
                    AuraColors.IncreaseMode
                }
            )
        }

        StatCardType.COMPLETED_POMODOROS -> {
            val isDecrease = trendValue < 0
            val absValue = kotlin.math.abs(trendValue)

            TrendUiState(
                text = if (isDecrease) {
                    "↓ -$absValue${if (isCompact) "" else " so với hôm qua"}"
                } else {
                    "↑ +$absValue${if (isCompact) "" else " so với hôm qua"}"
                },
                color = if (isDecrease) {
                    AuraColors.DecreaseMode
                } else {
                    AuraColors.IncreaseMode
                }
            )
        }

        StatCardType.BEST_STREAK -> {
            TrendUiState(
                text = if (isCompact) "★ Kỷ lục: $trendValue" else "⭐ Kỷ lục: $trendValue ngày",
                color = AuraColors.BestStreakDay
            )
        }
    }
}

class HomeUiViewModel(
    private val repository: UserAppStateRepositoryV2 = DependencyRegistry.userAppStateRepositoryV2,
    private val learningSessionManager: LearningSessionManager = DependencyRegistry.learningSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState()
    )

    val uiState : StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDataStatsSection()
        loadDataRecentActivitySection()
    }

    fun loadDataStatsSection() {
        viewModelScope.launch {
            val homeUiStats = learningSessionManager.getHomeDashboardStats()
            _uiState.update { state ->
                state.copy(
                    homeUiStats = homeUiStats
                )
            }
        }
    }

    private fun loadDataRecentActivitySection() {

    }
}