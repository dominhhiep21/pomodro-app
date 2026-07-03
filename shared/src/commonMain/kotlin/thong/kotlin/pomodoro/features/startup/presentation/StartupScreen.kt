package thong.kotlin.pomodoro.features.startup.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.landspace_startup_bg
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.core.notification.NotificationManager
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.onboarding.presentation.OnboardingScreen
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2
import thong.kotlin.pomodoro.features.session.presentation.SessionHistoryScreen

class StartupLoadingScreen(
    private val repositoryV2: UserAppStateRepositoryV2 = DependencyRegistry.userAppStateRepositoryV2
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(repositoryV2) {
            delay(1000)

            val userSettings = runCatching {
                repositoryV2.getUserSettings()
            }.getOrNull()

            val nextScreen = if (userSettings?.hasCompletedOnboarding == true) {
                SessionHistoryScreen()
            } else {
                OnboardingScreen()
            }

            navigator.replace(nextScreen)
        }

        StartupScreenUI()
    }
}

@Composable
private fun StartupScreenUI() {
    AuraBackground(
        landscapeImageRes = Res.drawable.landspace_startup_bg,
        blurRadius = 0f,
        overlayAlpha = 0.2f
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val isLandscape = maxWidth > maxHeight

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        end = if (isLandscape) 64.dp else 0.dp,
                        bottom = if (isLandscape) 0.dp else 64.dp
                    ),
                contentAlignment = if (isLandscape) {
                    Alignment.CenterEnd
                } else {
                    Alignment.BottomCenter
                }
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(36.dp),
                    strokeWidth = 3.dp
                )
            }
        }
    }
}