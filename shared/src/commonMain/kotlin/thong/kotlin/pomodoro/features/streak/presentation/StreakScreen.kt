package thong.kotlin.pomodoro.features.streak.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.streak.domain.StreakRepository
import thong.kotlin.pomodoro.features.streak.presentation.components.*

class StreakScreen(
    private val streakRepository: StreakRepository
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: StreakViewModel = viewModel { StreakViewModel(streakRepository) }
        val uiState by viewModel.uiState.collectAsState()
        StreakScreenContent(uiState = uiState)
    }
}

@Composable
internal fun StreakScreenContent(uiState: StreakUiState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.42f),
                contentAlignment = Alignment.Center
            ) {
                RivePetView(
                    onTap = {},
                    modifier = Modifier.fillMaxSize(0.85f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StreakCounterCard(
                    label = "Current Streak",
                    count = uiState.currentStreak,
                    accentColor = AuraColors.WorkMode,
                    icon = { RiveFireIcon(modifier = Modifier.fillMaxSize()) },
                    modifier = Modifier.weight(1f)
                )
                StreakCounterCard(
                    label = "Longest Streak",
                    count = uiState.longestStreak,
                    accentColor = Color(0xFFF59E0B),
                    icon = { RiveStarIcon(modifier = Modifier.fillMaxSize()) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Activity",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AuraColors.TextSecondary,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            CalendarHeatMap(
                history = uiState.heatMapData,
                accentColor = AuraColors.WorkMode,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
