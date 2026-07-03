package thong.kotlin.pomodoro.features.background.model

import androidx.compose.ui.graphics.Color

/**
 * Defines the type of background to be rendered.
 */
enum class BackgroundType {
    STATIC,
    ANIMATED_GRADIENT,
    DYNAMIC_AURA
}

/**
 * Defines the performance level for animations.
 */
enum class PerformanceMode {
    POWER_SAVER, // Reduced frame rate, fewer elements
    BALANCED,    // Standard frame rate
    HIGH_QUALITY // Max frame rate, high complexity
}

/**
 * Configuration for the background visuals and behavior.
 */
data class BackgroundConfig(
    val type: BackgroundType = BackgroundType.DYNAMIC_AURA,
    val performanceMode: PerformanceMode = PerformanceMode.BALANCED,
    val primaryColor: Color = Color(0xFF6200EE),
    val secondaryColor: Color = Color(0xFF03DAC6),
    val speed: Float = 1.0f,
    val intensity: Float = 0.5f
)
