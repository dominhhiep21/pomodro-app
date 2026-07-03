package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Streak-themed animated background inspired by "Modern Dark (Cinema Mobile)" style.
 *
 * Combines:
 * - Deep dark gradient base (dark indigo → near-black)
 * - Animated ambient fire-glow blobs with slow oscillation (matches streak fire theme)
 * - Subtle warm accent glow for immersive cinematic feel
 *
 * Self-contained within the streak feature — does NOT affect any other screens.
 */
@Composable
fun StreakBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        StreakAmbientCanvas(modifier = Modifier.fillMaxSize())
        content()
    }
}

@Composable
private fun StreakAmbientCanvas(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "StreakBgTransition")

    // Slow oscillation for blob 1 (warm fire — top-right area)
    val blob1OffsetX by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob1X"
    )
    val blob1OffsetY by infiniteTransition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob1Y"
    )

    // Slow oscillation for blob 2 (deeper orange/amber — bottom-left area)
    val blob2OffsetX by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob2X"
    )
    val blob2OffsetY by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 0.80f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob2Y"
    )

    // Subtle pulse for blob 3 (rose/streak accent — center-left)
    val blob3OffsetX by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob3X"
    )
    val blob3OffsetY by infiniteTransition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 13000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob3Y"
    )

    // Pre-calculated colors — avoid allocation in draw scope
    val bgGradientColors = remember {
        listOf(
            Color(0xFF0C0A12), // Very dark warm purple-black (top)
            Color(0xFF09090B)  // Deep black matching AuraColors.Background (bottom)
        )
    }

    // Fire blob 1: warm orange glow
    val blob1Color = remember { Color(0xFFF97316) } // Orange 500
    // Fire blob 2: deep amber
    val blob2Color = remember { Color(0xFFEA580C) } // Orange 600
    // Streak accent blob 3: rose (matching WorkMode / streak fire)
    val blob3Color = remember { Color(0xFFF43F5E) } // Rose 500

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Base gradient: dark warm purple-black → pure black
        drawRect(
            brush = Brush.verticalGradient(
                colors = bgGradientColors,
                startY = 0f,
                endY = h
            )
        )

        // Blob 1: Warm fire glow (top-right, large radius)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    blob1Color.copy(alpha = 0.09f),
                    blob1Color.copy(alpha = 0.04f),
                    Color.Transparent
                ),
                center = Offset(w * blob1OffsetX, h * blob1OffsetY),
                radius = w * 0.55f
            )
        )

        // Blob 2: Deep amber glow (bottom-left, medium radius)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    blob2Color.copy(alpha = 0.07f),
                    blob2Color.copy(alpha = 0.03f),
                    Color.Transparent
                ),
                center = Offset(w * blob2OffsetX, h * blob2OffsetY),
                radius = w * 0.45f
            )
        )

        // Blob 3: Rose streak accent (center-left, subtle)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    blob3Color.copy(alpha = 0.06f),
                    blob3Color.copy(alpha = 0.02f),
                    Color.Transparent
                ),
                center = Offset(w * blob3OffsetX, h * blob3OffsetY),
                radius = w * 0.40f
            )
        )
    }
}
