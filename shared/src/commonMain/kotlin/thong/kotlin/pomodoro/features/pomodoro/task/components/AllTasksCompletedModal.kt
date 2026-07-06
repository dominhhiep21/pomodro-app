package thong.kotlin.pomodoro.features.pomodoro.task.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TotallyPomodoroUiState

@Composable
fun AllTasksCompletedModal(
    totallyPomodoroUiState: TotallyPomodoroUiState,
    onSkipAndGetPoints: () -> Unit,
    onContinueAndAddTask: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            backgroundColor = Color.Black.copy(alpha = 0.95f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Icon Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = AuraColors.ShortBreakMode,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tuyệt vời!",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bạn đã hoàn thành tất cả công việc đề ra. Bạn muốn kết thúc sớm hay tiếp tục thêm công việc mới?",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuraButton(
                    onClick = onSkipAndGetPoints,
                    horizontalPadding = 32.dp,
                    verticalPadding = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "KẾT THÚC VÀ NHẬN ĐIỂM",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onContinueAndAddTask,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "TIẾP TỤC VÀ THÊM TASK",
                        color = AuraColors.ShortBreakMode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
