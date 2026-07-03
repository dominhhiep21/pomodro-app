package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AuraCircularProgress(
    progress: Float,                // Giá trị từ 0.0f đến 1.0f
    progressBrush: Brush,           // Dải màu gradient (ví dụ: AuraGradients.WorkFlow)
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 6.dp,
    animate: Boolean = true         // OPTIMIZATION: Toggle animation
) {
    // Mượt hóa chuyển động của thanh tiến độ
    val finalProgress = if (animate) {
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 1000)
        )
        animatedProgress
    } else {
        progress
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()

            // 1. Vẽ vòng tròn nền (Track) phía sau
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                style = Stroke(width = strokeWidthPx)
            )

            // 2. Vẽ vòng tiến trình (Arc)
            drawArc(
                brush = progressBrush,
                startAngle = -90f,
                sweepAngle = 360f * finalProgress,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }
    }
}
