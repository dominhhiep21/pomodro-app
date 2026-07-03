package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.*
import thong.kotlin.pomodoro.core.designsystem.theme.AuraAnimations
import thong.kotlin.pomodoro.core.designsystem.theme.AuraGradients

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color.White.copy(alpha = 0.07f),
    animateColor: Boolean = true,                            // OPTIMIZATION: Toggle animation
    backgroundBrush: Brush? = null,
    borderBrush: Brush = AuraGradients.GlassBorder,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    // OPTIMIZATION: Only animate if explicitly requested
    val finalBgColor = if (animateColor) {
        val animatedBgColor by animateColorAsState(
            targetValue = backgroundColor,
            animationSpec = AuraAnimations.colorTween(),
            label = "GlassBoxBackgroundAnimation"
        )
        animatedBgColor
    } else {
        backgroundColor
    }

    val backgroundModifier = if (backgroundBrush != null) {
        Modifier.background(backgroundBrush)
    } else {
        Modifier.background(finalBgColor)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(backgroundModifier)
            .border(width = 1.dp, brush = borderBrush, shape = shape),
        contentAlignment = contentAlignment,
        content = content
    )
}