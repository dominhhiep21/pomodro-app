package thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.components.AuraHeader
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.TimerSizes
import thong.kotlin.pomodoro.features.pomodoro.timer.state.PomodoroUiState

@Composable
fun TimerSection(
    uiState: PomodoroUiState,
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
    val timerBackgroundColor = remember(uiState.currentMode) {
        when (uiState.currentMode) {
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
                            imageVector = Icons.Default.Star,
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

        DailyPomoBadge(count = uiState.pomodorosToday)

        if (compact) {
            CompactTimerControls(
                uiState = uiState,
                themeColor = themeColor,
                timerBackgroundColor = timerBackgroundColor,
                timerSizes = timerSizes,
                onToggleTimer = onToggleTimer,
                onResetTimer = onResetTimer,
                onSkipTimer = onSkipTimer
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            TimerCircle(
                uiState = uiState,
                themeColor = themeColor,
                timerBackgroundColor = timerBackgroundColor,
                sizes = timerSizes
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isJustEndedBreak) {
                BreakEndBanner()
                Spacer(modifier = Modifier.height(16.dp))
            }

            TimerControlButtonsHorizontal(
                isActive = uiState.isActive,
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
    uiState: PomodoroUiState,
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
        TimerCircle(
            uiState = uiState,
            themeColor = themeColor,
            timerBackgroundColor = timerBackgroundColor,
            sizes = timerSizes
        )

        Spacer(modifier = Modifier.width(16.dp))

        TimerControlButtonsVertical(
            isActive = uiState.isActive,
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

@Composable
private fun TimerControlButtonsHorizontal(
    isActive: Boolean,
    themeColor: Color,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val buttonGap = 10.dp
        val buttonWidth = (maxWidth - buttonGap * 2) / 3
        val buttonModifier = Modifier
            .width(buttonWidth)
            .height(48.dp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(buttonGap),
            modifier = Modifier.fillMaxWidth()
        ) {
            AuraButton(
                onClick = onResetTimer,
                modifier = buttonModifier,
                horizontalPadding = 4.dp,
                verticalPadding = 8.dp,
                fillContent = true
            ) {
                TimerButtonText("Đặt lại", color = AuraColors.TextSecondary, fontSize = 13.sp)
            }

            AuraButton(
                onClick = onToggleTimer,
                modifier = buttonModifier,
                horizontalPadding = 4.dp,
                verticalPadding = 8.dp,
                fillContent = true
            ) {
                TimerButtonText(
                    text = if (isActive) "Tạm dừng" else "Bắt đầu",
                    color = themeColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AuraButton(
                onClick = onSkipTimer,
                modifier = buttonModifier,
                horizontalPadding = 4.dp,
                verticalPadding = 8.dp,
                fillContent = true
            ) {
                TimerButtonText("Bỏ qua", color = AuraColors.TextSecondary, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun TimerControlButtonsVertical(
    isActive: Boolean,
    themeColor: Color,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit
) {
    val buttonModifier = Modifier
        .fillMaxWidth()
        .height(48.dp)

    Column(
        modifier = Modifier.width(132.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AuraButton(onClick = onResetTimer, modifier = buttonModifier) {
            TimerButtonText("Đặt lại", color = AuraColors.TextSecondary, fontSize = 13.sp)
        }

        AuraButton(onClick = onToggleTimer, modifier = buttonModifier) {
            TimerButtonText(
                text = if (isActive) "Tạm dừng" else "Bắt đầu",
                color = themeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        AuraButton(onClick = onSkipTimer, modifier = buttonModifier) {
            TimerButtonText("Bỏ qua", color = AuraColors.TextSecondary, fontSize = 13.sp)
        }
    }
}