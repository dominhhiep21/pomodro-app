package thong.kotlin.pomodoro.features.focus.score.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.focus.score.domain.FocusScoreInput
import thong.kotlin.pomodoro.features.focus.score.domain.calculatePoint

@Composable
fun RoundFocusScoreModal(
    scoreInput: FocusScoreInput,
    onDismiss: () -> Unit
) {
    val score = scoreInput.calculatePoint()
    var secondsLeft by remember { mutableIntStateOf(15) }

    // Tự động đóng sau 15 giây
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(32.dp),
            backgroundColor = Color.Black.copy(alpha = 0.8f)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Kết quả phiên này",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$score",
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Focus Score",
                    color = AuraColors.ShortBreakMode,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (score >= 80) "Tuyệt vời! Bạn đã rất tập trung."
                    else if (score >= 50) "Khá tốt! Hãy tiếp tục duy trì nhé."
                    else "Hãy cố gắng tập trung hơn ở phiên tới!",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tự động đóng sau ${secondsLeft}s",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuraButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tiếp tục",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
