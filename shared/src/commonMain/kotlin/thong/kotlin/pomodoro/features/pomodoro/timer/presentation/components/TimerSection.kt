package thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ZoomInMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.AuraHeader
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro._base.components.BreakEndBanner
import thong.kotlin.pomodoro.features.pomodoro._base.components.DailyPomoBadge
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TimerUiState
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.WorkspaceUiState
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.TimerSizes

@Composable
fun TimerSectionComponent(
    timerUiState: TimerUiState,
    workspaceUiState: WorkspaceUiState,
    themeColor: Color,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit,
    onToggleSettings: () -> Unit,
    onExit: () -> Unit = {},
    onToggleCompactMode: () -> Unit = {},
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val timerBackgroundColor = remember(timerUiState.currentMode) {
        when (timerUiState.currentMode) {
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
        AuraHeader(
            title = "Aura Pomo",
            subtitle = "Tìm kiếm dòng chảy học tập",
            actionButton = {
                Row {
                    IconButton(onClick = onToggleCompactMode) {
                        Icon(
                            imageVector = Icons.Default.ZoomInMap,
                            contentDescription = "Compact Mode",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(onClick = onToggleSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(onClick = onExit) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Exit to Style Selection",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        )

        DailyPomoBadge(count = timerUiState.pomodorosToday)

        if (compact) {
            CompactTimerControls(
                workspaceUiState = workspaceUiState,
                timerUiState = timerUiState,
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
                workspaceUiState = workspaceUiState,
                timerUiState = timerUiState,
                themeColor = themeColor,
                timerBackgroundColor = timerBackgroundColor,
                sizes = timerSizes
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (workspaceUiState.isJustEndedBreak) {
                BreakEndBanner()
                Spacer(modifier = Modifier.height(16.dp))
            }

            TimerControlButtonsHorizontal(
                isActive = timerUiState.isActive,
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
    workspaceUiState: WorkspaceUiState,
    timerUiState: TimerUiState,
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
            workspaceUiState = workspaceUiState,
            timerUiState = timerUiState,
            themeColor = themeColor,
            timerBackgroundColor = timerBackgroundColor,
            sizes = timerSizes
        )

        Spacer(modifier = Modifier.width(16.dp))

        TimerControlButtonsVertical(
            isActive = timerUiState.isActive,
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