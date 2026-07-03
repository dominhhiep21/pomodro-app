package thong.kotlin.pomodoro.features.background.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import thong.kotlin.pomodoro.features.background.model.PerformanceMode

@Composable
fun AnimatedGradientBackground(
    primaryColor: Color,
    secondaryColor: Color,
    performanceMode: PerformanceMode,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GradientTransition")
    
    val durationMillis = remember(performanceMode) {
        when (performanceMode) {
            PerformanceMode.POWER_SAVER -> 12000
            PerformanceMode.BALANCED -> 8000
            PerformanceMode.HIGH_QUALITY -> 4000
        }
    }

    val offsetAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OffsetAnimation"
    )

    // Pre-calculate color list to avoid allocation in DrawScope
    val gradientColors = remember(primaryColor, secondaryColor) {
        listOf(primaryColor, secondaryColor)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Calculate offsets based on animation value
        val startOffset = Offset(x = width * offsetAnim, y = 0f)
        val endOffset = Offset(x = width * (1f - offsetAnim), y = height)

        drawRect(
            brush = Brush.linearGradient(
                colors = gradientColors,
                start = startOffset,
                end = endOffset
            ),
            size = size
        )
    }
}
