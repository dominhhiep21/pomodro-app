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
 * Streak-themed cinematic animated background.
 *
 * Design: "Modern Dark Cinema Mobile" (ui-ux-pro-max)
 * - Deep gradient base (#0a0a0f → #020203) — NOT pure black
 * - 3 animated ambient glow blobs with HIGH visibility (opacity 0.12-0.22)
 * - Warm fire/streak palette (orange + rose + amber)
 * - Slow oscillation (7-13s) for premium atmospheric feel
 *
 * Performance: Uses Canvas + animateFloat (GPU-friendly, no recomposition).
 * Self-contained: Only used in streak feature.
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
    val infiniteTransition = rememberInfiniteTransition(label = "StreakBg")

    // ---- Blob 1: Large warm orange glow (top-right) ----
    val blob1X by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "B1X"
    )
    val blob1Y by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "B1Y"
    )

    // ---- Blob 2: Rose/fire glow (center-left, matching streak accent) ----
    val blob2X by infiniteTransition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "B2X"
    )
    val blob2Y by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.50f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "B2Y"
    )

    // ---- Blob 3: Deep amber glow (bottom area) ----
    val blob3X by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "B3X"
    )
    val blob3Y by infiniteTransition.animateFloat(
        initialValue = 0.70f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 13000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "B3Y"
    )

    // Pre-computed colors (avoid allocation in DrawScope)
    val bgColors = remember {
        listOf(
            Color(0xFF0F0A14), // Dark warm purple-black (top) — NOT pure black
            Color(0xFF050506), // Near-black (mid)
            Color(0xFF0A0708)  // Very dark warm brown-black (bottom)
        )
    }
    val blob1Color = remember { Color(0xFFF97316) }  // Orange 500 (fire)
    val blob2Color = remember { Color(0xFFF43F5E) }  // Rose 500 (streak accent)
    val blob3Color = remember { Color(0xFFEA580C) }  // Orange 600 (deep amber)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Base: 3-stop vertical gradient (dark warm purple → near-black → warm brown)
        drawRect(
            brush = Brush.verticalGradient(
                colors = bgColors,
                startY = 0f,
                endY = h
            )
        )

        // Blob 1: Large orange fire glow — VERY VISIBLE (0.18 peak)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    blob1Color.copy(alpha = 0.18f),
                    blob1Color.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(w * blob1X, h * blob1Y),
                radius = w * 0.6f
            )
        )

        // Blob 2: Rose/streak fire — center-left area (0.15 peak)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    blob2Color.copy(alpha = 0.15f),
                    blob2Color.copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = Offset(w * blob2X, h * blob2Y),
                radius = w * 0.5f
            )
        )

        // Blob 3: Deep amber — bottom area (0.12 peak)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    blob3Color.copy(alpha = 0.12f),
                    blob3Color.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(w * blob3X, h * blob3Y),
                radius = w * 0.45f
            )
        )
    }
}
