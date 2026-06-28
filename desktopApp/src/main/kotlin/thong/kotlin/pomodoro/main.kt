package thong.kotlin.pomodoro

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import thong.kotlin.pomodoro.core.media.JvmSoundManager
import thong.kotlin.pomodoro.core.notification.JvmNotificationManager
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.database.DatabaseDriverFactory

fun main() = application {
    val soundManager = JvmSoundManager.instance
    val notificationManager = JvmNotificationManager()
    // Khởi tạo Database context
    val database = AuraDatabase(
        DatabaseDriverFactory().createDriver()
    )


    Window(
        onCloseRequest = ::exitApplication,
        title = "Pomodrokotlin",
    ) {
        App(
            database = database,
            soundManager = soundManager,
            notificationManager = notificationManager
        )
    }
}