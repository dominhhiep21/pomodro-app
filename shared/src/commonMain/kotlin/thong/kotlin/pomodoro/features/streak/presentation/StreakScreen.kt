package thong.kotlin.pomodoro.features.streak.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    // Entry animation for the whole screen
    var screenVisible by remember { mutableStateOf(false) }
    val screenAlpha by animateFloatAsState(
        targetValue = if (screenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "ScreenAlpha"
    )
    LaunchedEffect(Unit) { screenVisible = true }

    StreakBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(screenAlpha)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== Section 1: Pet Animation (hero area) =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)   // Square container for pet
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                RivePetView(
                    onTap = {},
                    modifier = Modifier.fillMaxSize(0.88f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Section 2: Streak Counter Cards (centered) =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Section 3: Activity Heat Map =====
            Text(
                text = "Activity",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AuraColors.TextPrimary,
                letterSpacing = 0.3.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 10.dp)
            )
            CalendarHeatMap(
                history = uiState.heatMapData,
                accentColor = AuraColors.WorkMode,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
