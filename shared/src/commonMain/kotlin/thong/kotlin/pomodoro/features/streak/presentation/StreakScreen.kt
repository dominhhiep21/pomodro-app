package thong.kotlin.pomodoro.features.streak.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        val petState = viewModel.getPetState()

        StreakScreenContent(uiState = uiState, petState = petState)
    }
}

@Composable
internal fun StreakScreenContent(
    uiState: StreakUiState,
    petState: PetState
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pet area (~40% of screen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            contentAlignment = Alignment.Center
        ) {
            RivePetView(
                petState = petState,
                onTap = { /* trigger klik interaction */ },
                modifier = Modifier.fillMaxSize(0.8f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Streak counter cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StreakCounterCard(
                label = "Current",
                count = uiState.currentStreak,
                icon = { RiveFireIcon(modifier = Modifier.fillMaxSize()) },
                modifier = Modifier.weight(1f)
            )
            StreakCounterCard(
                label = "Longest",
                count = uiState.longestStreak,
                icon = { RiveStarIcon(modifier = Modifier.fillMaxSize()) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Calendar Heat Map
        Text(
            text = "Activity",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = AuraColors.textSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        CalendarHeatMap(
            history = uiState.heatMapData,
            modifier = Modifier.fillMaxWidth().weight(0.3f)
        )
    }
}
