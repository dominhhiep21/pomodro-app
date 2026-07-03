package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors

@Composable
fun StreakCounterCard(
    label: String,
    count: Int,
    accentColor: Color,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)

    // Bounce animation on entry
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "CardScale"
    )
    LaunchedEffect(Unit) { visible = true }

    // Pulsing border glow
    val infiniteTransition = rememberInfiniteTransition(label = "BorderGlow")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BorderAlpha"
    )

    // Card background: gradient from accent to transparent for depth
    val cardBgBrush = remember(accentColor) {
        Brush.verticalGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.12f),
                accentColor.copy(alpha = 0.04f),
                Color.White.copy(alpha = 0.03f)
            )
        )
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(cardBgBrush)
            .border(
                width = 1.5.dp,
                color = accentColor.copy(alpha = borderAlpha),
                shape = shape
            )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(modifier = Modifier.size(40.dp)) {
                icon()
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Count number - large and bold
            Text(
                text = count.toString(),
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuraColors.TextPrimary,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Label
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = accentColor.copy(alpha = 0.9f),
                letterSpacing = 0.4.sp
            )
        }
    }
}
