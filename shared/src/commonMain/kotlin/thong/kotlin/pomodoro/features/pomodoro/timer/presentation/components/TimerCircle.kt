package thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.AuraCircularProgress
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.designsystem.theme.AuraGradients
import thong.kotlin.pomodoro.core.utils.formatToMmSs
import thong.kotlin.pomodoro.features.background.model.PerformanceMode
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro._base.domain.totalSeconds
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.TimerSizes
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TotallyPomodoroUiState

@Composable
fun TimerCircleComponent(
    totallyPomodoroUiState: TotallyPomodoroUiState,
    themeColor: Color,
    timerBackgroundColor: Color,
    sizes: TimerSizes
) {
    val performanceMode = totallyPomodoroUiState.workspaceUiState.backgroundConfig?.performanceMode

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(sizes.outerSize)
    ) {
        val totalSeconds = remember(totallyPomodoroUiState.timerUiState.currentMode, totallyPomodoroUiState.timerUiState.config) {
            totallyPomodoroUiState.timerUiState.currentMode.totalSeconds(totallyPomodoroUiState.timerUiState.config)
        }
        val progress = remember(totalSeconds, totallyPomodoroUiState.timerUiState.timeLeft) {
            (totalSeconds - totallyPomodoroUiState.timerUiState.timeLeft).toFloat() / totalSeconds
        }
        val progressBrush = remember(totallyPomodoroUiState.timerUiState.currentMode) {
            if (totallyPomodoroUiState.timerUiState.currentMode == PomodoroMode.WORK) {
                AuraGradients.WorkFlow
            } else {
                AuraGradients.BreakFlow
            }
        }

        AuraCircularProgress(
            progress = progress,
            progressBrush = progressBrush,
            modifier = Modifier.size(sizes.progressSize),
            strokeWidth = 4.dp,
            animate = performanceMode != PerformanceMode.POWER_SAVER
        )

        GlassBox(
            shape = RoundedCornerShape(sizes.cornerRadius),
            backgroundColor = timerBackgroundColor,
            animateColor = performanceMode != PerformanceMode.POWER_SAVER,
            modifier = Modifier.size(sizes.glassSize)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = totallyPomodoroUiState.timerUiState.timeLeft.formatToMmSs(),
                    color = AuraColors.TextPrimary,
                    fontSize = sizes.textSize,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = totallyPomodoroUiState.timerUiState.currentMode.label.uppercase(),
                    color = themeColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
fun CompactFloatingTimerComponent(
    timeLeft: Int,
    mode: PomodoroMode,
    isActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 140.dp,
    height: Dp = 64.dp,
    timeFontSize: TextUnit = 20.sp,
    buttonSize: Dp = 36.dp,
    showExitShortcut: Boolean = false,
    onToggleSettings: () -> Unit = {},
    onExitClick: () -> Unit = {}
) {
    val minutes = (timeLeft / 60).toString().padStart(2, '0')
    val seconds = (timeLeft % 60).toString().padStart(2, '0')

    val modeColor = when (mode) {
        PomodoroMode.WORK -> AuraColors.WorkMode
        PomodoroMode.SHORT_BREAK -> AuraColors.ShortBreakMode
        PomodoroMode.LONG_BREAK -> AuraColors.LongBreakMode
    }
    val modeText = when (mode) {
        PomodoroMode.WORK -> "WORK"
        PomodoroMode.SHORT_BREAK -> "BREAK"
        PomodoroMode.LONG_BREAK -> "REST"
    }

    Row(
        modifier = modifier.wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showExitShortcut) {
            IconButton(
                onClick = onExitClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.ZoomOutMap, "Exit", tint = Color.White)
            }
        }

        GlassBox(
            modifier = Modifier
                .width(width)
                .height(height),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = modeText,
                        color = modeColor,
                        fontSize = (timeFontSize.value * 0.5).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "$minutes:$seconds",
                        color = Color.White,
                        fontSize = timeFontSize,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                IconButton(
                    onClick = onToggle,
                    modifier = Modifier
                        .size(buttonSize)
                        .background(modeColor, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isActive) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(buttonSize * 0.6f)
                    )
                }
            }
        }

        IconButton(
            onClick = onToggleSettings,
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}