package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AuraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillContent: Boolean = false,
    horizontalPadding: Dp = 24.dp,
    verticalPadding: Dp = 14.dp,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1.0f,
        label = "ButtonScaleAnimation"
    )

    val contentModifier = if (fillContent) {
        Modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
    } else {
        Modifier
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
    }

    GlassBox(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = contentModifier.graphicsLayer {
                alpha = if (enabled) 1f else 0.4f
            },
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
