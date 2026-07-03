package thong.kotlin.pomodoro.core.notification

interface NotificationManager {
    fun showNotification(title: String, message: String)
    fun requestPermission()
}
