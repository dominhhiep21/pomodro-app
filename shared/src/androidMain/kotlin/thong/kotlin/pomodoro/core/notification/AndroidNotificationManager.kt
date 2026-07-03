package thong.kotlin.pomodoro.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import thong.kotlin.pomodoro.shared.R
import android.app.NotificationManager as SystemNotificationManager
import androidx.core.graphics.createBitmap


class AndroidNotificationManager(private val context: Context) : NotificationManager {
    private val channelId = "pomodoro_timer_channel"
    private val channelName = "Pomodoro Timer"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = SystemNotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Notifications for Pomodoro timer events"
            }
            val notificationManager: SystemNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as SystemNotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun showNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_raven_notification)
            .setLargeIcon(getBitmapFromVectorDrawable(context, R.drawable.ic_raven_notification))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun requestPermission() {
        // Permission request logic is typically handled in the Activity
        // for Compose, we often use rememberLauncherForActivityResult
    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null


        // Tạo một Bitmap với kích thước của Vector Drawable
        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)


        // Dùng Canvas để vẽ drawable lên Bitmap
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}
