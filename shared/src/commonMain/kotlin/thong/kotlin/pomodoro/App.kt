package thong.kotlin.pomodoro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import thong.kotlin.pomodoro.core.designsystem.components.AuraToastHost
import thong.kotlin.pomodoro.core.designsystem.theme.AuraTheme
import thong.kotlin.pomodoro.core.notification.toast.AppToastHostState
import thong.kotlin.pomodoro.core.notification.toast.AuraToast
import thong.kotlin.pomodoro.features.startup.presentation.StartupLoadingScreen

@OptIn(ExperimentalVoyagerApi::class)
@Composable
@Preview
fun Aura() {
    val toastHostState = remember { AppToastHostState() }

    LaunchedEffect(toastHostState) {
        AuraToast.attach(toastHostState)
    }

    DisposableEffect(Unit) {
        onDispose {
            AuraToast.detach()
        }
    }

    AuraTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ProvideNavigatorLifecycleKMPSupport {
                Navigator(StartupLoadingScreen()) { navigator ->
                    FadeTransition(navigator)
                }
            }

            AuraToastHost(
                toastHostState = toastHostState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}