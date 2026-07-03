package thong.kotlin.pomodoro.core.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable

/**
 * Định nghĩa các tọa độ màn hình có trong hệ thống
 */
sealed interface AuraScreen {
    data object Splash : AuraScreen
    data object Onboarding : AuraScreen
    data object LearningStyleSelection : AuraScreen
    data object MainApp : AuraScreen
}

/**
 * Bộ điều hướng Core Navigator: Hỗ trợ chuyển cảnh kèm hiệu ứng mờ dần (Crossfade) mượt mà
 */
@Composable
fun AuraNavigator(
    currentScreen: AuraScreen,
    content: @Composable (AuraScreen) -> Unit
) {
    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            // Hiệu ứng mượt mà khi đổi màn hình (Fade In + Fade Out trong 400ms)
            fadeIn(animationSpec = tween(400)) togetherWith
                    fadeOut(animationSpec = tween(400))
        },
        label = "ScreenTransitionAnimation"
    ) { screen ->
        content(screen)
    }
}