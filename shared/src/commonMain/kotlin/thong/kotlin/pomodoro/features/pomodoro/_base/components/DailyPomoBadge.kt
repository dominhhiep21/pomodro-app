package thong.kotlin.pomodoro.features.pomodoro._base.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox

@Composable
fun DailyPomoBadge(count: Int) {
    GlassBox(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        Text(
            text = "🔥 $count Pomo trong phiên học",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}