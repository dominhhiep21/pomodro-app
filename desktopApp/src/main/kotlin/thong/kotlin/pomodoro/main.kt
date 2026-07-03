package thong.kotlin.pomodoro

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import thong.kotlin.pomodoro.core.media.JvmSoundManager
import thong.kotlin.pomodoro.core.notification.JvmNotificationManager
import thong.kotlin.pomodoro.database.DatabaseDriverFactory
import thong.kotlin.pomodoro.di.DependencyRegistry

fun main() = application {
    val soundManager = JvmSoundManager.instance
    val notificationManager = JvmNotificationManager()
    val database = DatabaseDriverFactory().createDriver()

    DependencyRegistry.initDatabase(database)
    DependencyRegistry.initSoundManager(soundManager)
    DependencyRegistry.initNotificationManager(notificationManager)

    Window(
        onCloseRequest = {
            notificationManager.dispose()
            exitApplication()
        },
        title = "Pomodoro Desktop"
    ) {
        App()
    }
}