package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AuraCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    activeColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    // Tự động mượt hóa màu nền từ trong suốt sang màu chủ đạo
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) activeColor else Color.Transparent
    )

    // Tự động mượt hóa đường viền
    val borderColor by animateColorAsState(
        targetValue = if (checked) Color.Transparent else Color.White.copy(alpha = 0.3f)
    )

    // Hiệu ứng bong bóng nảy nhẹ (Pop) khi được tích chọn
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.0f else 0.9f
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = Color.Black, // Dấu tích đen nổi bật trên nền màu sáng
                modifier = Modifier.size(size * 0.65f)
            )
        }
    }
}