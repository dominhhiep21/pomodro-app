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
import kotlin.random.Random

/**
 * Streak-themed cinematic background with starry sky + fire ambient glow.
 *
 * Layers (bottom to top):
 * 1. Deep gradient base (dark midnight blue → deep black)
 * 2. Twinkling stars (random positions, animated alpha)
 * 3. Warm fire-glow blobs (orange + rose, slow oscillation)
 *
 * The starry sky creates the cosmic/magical feel matching app's dark theme,
 * while fire blobs represent streak energy.
 */
@Composable
fun StreakBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        StreakStarryCanvas(modifier = Modifier.fillMaxSize())
        content()
    }
}

// Data class for a single star
private data class Star(
    val xRatio: Float,  // 0..1 position ratio
    val yRatio: Float,
    val size: Float,    // radius in px
    val baseAlpha: Float,
    val twinkleSpeed: Int  // ms for one twinkle cycle
)

@Composable
private fun StreakStarryCanvas(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "StreakBg")

    // Generate stars once (deterministic seed for consistent layout)
    val stars = remember {
        val rng = Random(42)
        List(60) {
            Star(
                xRatio = rng.nextFloat(),
                yRatio = rng.nextFloat(),
                size = rng.nextFloat() * 1.5f + 0.5f,  // 0.5 - 2.0 px
                baseAlpha = rng.nextFloat() * 0.4f + 0.3f,  // 0.3 - 0.7
                twinkleSpeed = rng.nextInt(2000, 5000)
            )
        }
    }

    // Animate twinkle groups (3 groups to avoid 60 individual animations)
    val twinkle1 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Twinkle1"
    )
    val twinkle2 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Twinkle2"
    )
    val twinkle3 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Twinkle3"
    )

    // ---- Fire glow blob animations ----
    val blob1X by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(8000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "B1X"
    )
    val blob1Y by infiniteTransition.animateFloat(
        initialValue = 0.05f, targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            tween(10000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "B1Y"
    )
    val blob2X by infiniteTransition.animateFloat(
        initialValue = 0.10f, targetValue = 0.30f,
        animationSpec = infiniteRepeatable(
            tween(12000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "B2X"
    )
    val blob2Y by infiniteTransition.animateFloat(
        initialValue = 0.38f, targetValue = 0.52f,
        animationSpec = infiniteRepeatable(
            tween(9000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "B2Y"
    )
    val blob3X by infiniteTransition.animateFloat(
        initialValue = 0.55f, targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            tween(11000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "B3X"
    )
    val blob3Y by infiniteTransition.animateFloat(
        initialValue = 0.72f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            tween(13000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "B3Y"
    )

    // Pre-computed colors
    val bgColors = remember {
        listOf(
            Color(0xFF0A0E1A), // Midnight blue-black (top) — starry sky base
            Color(0xFF060810), // Deep dark blue (mid)
            Color(0xFF050506)  // Near-black (bottom)
        )
    }
    val starColor = remember { Color.White }
    val blob1Color = remember { Color(0xFFF97316) }  // Orange 500
    val blob2Color = remember { Color(0xFFF43F5E) }  // Rose 500
    val blob3Color = remember { Color(0xFFEA580C) }  // Orange 600

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Layer 1: Deep midnight gradient background
        drawRect(
            brush = Brush.verticalGradient(
                colors = bgColors,
                startY = 0f,
                endY = h
            )
        )

        // Layer 2: Twinkling stars
        stars.forEachIndexed { index, star ->
            val twinkleFactor = when (index % 3) {
                0 -> twinkle1
                1 -> twinkle2
                else -> twinkle3
            }
            val alpha = star.baseAlpha * twinkleFactor

            drawCircle(
                color = starColor.copy(alpha = alpha),
                radius = star.size,
                center = Offset(w * star.xRatio, h * star.yRatio)
            )
        }

        // Layer 3: Fire glow blobs (warm ambient light)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    blob1Color.copy(alpha = 0.16f),
                    blob1Color.copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = Offset(w * blob1X, h * blob1Y),
                radius = w * 0.55f
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    blob2Color.copy(alpha = 0.13f),
                    blob2Color.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(w * blob2X, h * blob2Y),
                radius = w * 0.45f
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    blob3Color.copy(alpha = 0.10f),
                    blob3Color.copy(alpha = 0.04f),
                    Color.Transparent
                ),
                center = Offset(w * blob3X, h * blob3Y),
                radius = w * 0.40f
            )
        )
    }
}
