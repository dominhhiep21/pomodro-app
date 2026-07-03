package thong.kotlin.pomodoro.features.background.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import org.jetbrains.compose.resources.DrawableResource
import thong.kotlin.pomodoro.features.background.model.BackgroundConfig
import thong.kotlin.pomodoro.features.background.model.BackgroundType
import thong.kotlin.pomodoro.features.background.presentation.components.AnimatedGradientBackground
import thong.kotlin.pomodoro.features.background.presentation.components.DynamicAuraBackground
import thong.kotlin.pomodoro.features.background.presentation.components.StaticBackground

@Composable
fun DynamicBackground(
    config: BackgroundConfig,
    imageRes: DrawableResource? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    clip = true
                }
        ) {
            when (config.type) {
                BackgroundType.STATIC -> {
                    StaticBackground(imageRes = imageRes)
                }
                BackgroundType.ANIMATED_GRADIENT -> {
                    AnimatedGradientBackground(
                        primaryColor = config.primaryColor,
                        secondaryColor = config.secondaryColor,
                        performanceMode = config.performanceMode
                    )
                }
                BackgroundType.DYNAMIC_AURA -> {
                    DynamicAuraBackground(
                        primaryColor = config.primaryColor,
                        secondaryColor = config.secondaryColor,
                        performanceMode = config.performanceMode
                    )
                }
            }
        }

        content()
    }
}
