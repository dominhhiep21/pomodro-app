package thong.kotlin.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.navigator.Navigator
import thong.kotlin.pomodoro.core.designsystem.theme.AuraTheme
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.core.notification.NotificationManager
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.features.startup.presentation.StartupLoadingScreen

@OptIn(ExperimentalVoyagerApi::class)
@Composable
@Preview
fun App(
    database: AuraDatabase? = null,
    soundManager: SoundManager? = null,
    notificationManager: NotificationManager? = null
) {
    AuraTheme {
        ProvideNavigatorLifecycleKMPSupport {
            Navigator(screen = StartupLoadingScreen(database, soundManager, notificationManager))
        }
    }
}