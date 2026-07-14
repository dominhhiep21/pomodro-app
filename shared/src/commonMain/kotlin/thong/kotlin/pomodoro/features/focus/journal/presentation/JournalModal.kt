package thong.kotlin.pomodoro.features.focus.journal.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.components.AuraInputField
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors

@Composable
fun PomodoroJournalModal(
    onSave: (String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var achievements by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var secondsLeft by remember { mutableIntStateOf(30) }

    // Tự động đóng sau 30 giây nếu không tương tác
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
                    text = "Ghi lại thành tựu",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bạn đã hoàn thành được những gì trong phiên vừa rồi?",
                    color = AuraColors.TextSecondary,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                AuraInputField(
                    value = achievements,
                    onValueChange = { achievements = it },
                    placeholder = "Ví dụ: Hoàn thành thiết kế UI...",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Mức độ tập trung",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                FocusRatingSelector(
                    currentRating = rating,
                    onRatingSelected = { rating = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tự động bỏ qua sau ${secondsLeft}s",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AuraButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bỏ qua", color = Color.White)
                    }

                    AuraButton(
                        onClick = { onSave(achievements, rating) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Lưu lại", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FocusRatingSelector(
    currentRating: Int,
    onRatingSelected: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(5) { index ->
            val starIndex = index + 1
            val isSelected = starIndex <= currentRating
            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingSelected(starIndex) }
            )
        }
    }
}
