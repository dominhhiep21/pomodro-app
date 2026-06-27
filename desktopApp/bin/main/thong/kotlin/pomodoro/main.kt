package thong.kotlin.pomodoro

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import thong.kotlin.pomodoro.core.media.JvmSoundManager
import thong.kotlin.pomodoro.core.notification.JvmNotificationManager

fun main() = application {
    val soundManager = JvmSoundManager.instance
    val notificationManager = JvmNotificationManager()
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "Pomodrokotlin",
    ) {
        App(
            soundManager = soundManager,
            notificationManager = notificationManager
        )
    }
}