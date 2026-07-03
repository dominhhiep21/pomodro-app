package thong.kotlin.pomodoro.features.background.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import thong.kotlin.pomodoro.features.background.model.PerformanceMode
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DynamicAuraBackground(
    primaryColor: Color,
    secondaryColor: Color,
    performanceMode: PerformanceMode,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AuraTransition")

    // Reduce complexity for lower performance modes
    val auraCount = remember(performanceMode) {
        when (performanceMode) {
            PerformanceMode.POWER_SAVER -> 1
            PerformanceMode.BALANCED -> 2
            PerformanceMode.HIGH_QUALITY -> 4
        }
    }

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * kotlin.math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "TimeAnimation"
    )

    // Pre-calculate colors to avoid allocation in DrawScope
    val auraColor = remember(secondaryColor) { secondaryColor.copy(alpha = 0.3f) }
    val transparent = Color.Transparent
    val gradientColors = remember(auraColor) { listOf(auraColor, transparent) }
    val backgroundColor = remember(primaryColor) { primaryColor.copy(alpha = 0.8f) }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw main background color once
        drawRect(color = backgroundColor)

        // Draw moving aura spots
        for (i in 0 until auraCount) {
            val phaseShift = i * (2f * kotlin.math.PI.toFloat() / auraCount.toFloat())
            val radius = size.minDimension * 0.7f
            
            // Calculate position with smooth orbital movement
            val x = size.width / 2f + (size.width * 0.35f) * cos(time + phaseShift)
            val y = size.height / 2f + (size.height * 0.25f) * sin(time * 0.6f + phaseShift)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = gradientColors,
                    center = Offset(x, y),
                    radius = radius
                ),
                center = Offset(x, y),
                radius = radius
            )
        }
    }
}
