package thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors

@Composable
fun TimerControlButtonsHorizontal(
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
fun TimerControlButtonsVertical(
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