package thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro._base.components.BreakEndBanner
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.TimerSizes
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TotallyPomodoroUiState

@Composable
fun TimerSectionComponent(
    modifier: Modifier = Modifier,
    totallyPomodoroUiState: TotallyPomodoroUiState,
    themeColor: Color,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit,
    compact: Boolean = false
) {
    val timerBackgroundColor = remember(totallyPomodoroUiState.currentMode) {
        when (totallyPomodoroUiState.currentMode) {
            PomodoroMode.WORK -> AuraColors.WorkMode.copy(alpha = 0.15f)
            PomodoroMode.SHORT_BREAK -> AuraColors.ShortBreakMode.copy(alpha = 0.05f)
            PomodoroMode.LONG_BREAK -> AuraColors.LongBreakMode.copy(alpha = 0.05f)
        }
    }
    val timerSizes = remember(compact) { getTimerSizes(compact) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (compact) Arrangement.Center else Arrangement.Top
    ) {
        if (compact) {
            CompactTimerControls(
                totallyPomodoroUiState = totallyPomodoroUiState,
                themeColor = themeColor,
                timerBackgroundColor = timerBackgroundColor,
                timerSizes = timerSizes,
                onToggleTimer = onToggleTimer,
                onResetTimer = onResetTimer,
                onSkipTimer = onSkipTimer
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            TimerCircleComponent(
                totallyPomodoroUiState = totallyPomodoroUiState,
                themeColor = themeColor,
                timerBackgroundColor = timerBackgroundColor,
                sizes = timerSizes
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (totallyPomodoroUiState.timerUiState.isJustEndedBreak) {
                BreakEndBanner()
                Spacer(modifier = Modifier.height(16.dp))
            }

            TimerControlButtonsHorizontal(
                isActive = totallyPomodoroUiState.timerUiState.isActive,
                themeColor = themeColor,
                onToggleTimer = onToggleTimer,
                onResetTimer = onResetTimer,
                onSkipTimer = onSkipTimer
            )
        }
    }
}

@Composable
private fun CompactTimerControls(
    totallyPomodoroUiState: TotallyPomodoroUiState,
    themeColor: Color,
    timerBackgroundColor: Color,
    timerSizes: TimerSizes,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimerCircleComponent(
            totallyPomodoroUiState = totallyPomodoroUiState,
            themeColor = themeColor,
            timerBackgroundColor = timerBackgroundColor,
            sizes = timerSizes
        )

        Spacer(modifier = Modifier.width(16.dp))

        TimerControlButtonsVertical(
            isActive = totallyPomodoroUiState.timerUiState.isActive,
            themeColor = themeColor,
            onToggleTimer = onToggleTimer,
            onResetTimer = onResetTimer,
            onSkipTimer = onSkipTimer
        )
    }
}

private fun getTimerSizes(compact: Boolean): TimerSizes {
    return if (compact) {
        TimerSizes(190.dp, 206.dp, 184.dp, 92.dp, 42.sp)
    } else {
        TimerSizes(260.dp, 256.dp, 230.dp, 120.dp, 54.sp)
    }
}