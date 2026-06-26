package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors

@Composable
fun StreakCounterCard(
    label: String,
    count: Int,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    // Bounce animation on entry
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    LaunchedEffect(Unit) { isVisible = true }

    GlassBox(modifier = modifier.scale(scale)) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(32.dp)) {
                icon()
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = AuraColors.textPrimary
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = AuraColors.textSecondary
            )
        }
    }
}
