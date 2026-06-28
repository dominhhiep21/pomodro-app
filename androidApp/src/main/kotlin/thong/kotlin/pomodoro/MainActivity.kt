package thong.kotlin.pomodoro

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import thong.kotlin.pomodoro.core.media.AndroidSoundManager
import thong.kotlin.pomodoro.core.notification.AndroidNotificationManager
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.database.DatabaseDriverFactory

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Quyền đã được cấp hoặc bị từ chối
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Khởi tạo Database context
        val database = AuraDatabase(
            DatabaseDriverFactory(this).createDriver()
        )

        // Yêu cầu quyền thông báo trên Android 13+
        requestNotificationPermission()

        val soundManager = AndroidSoundManager.getInstance(this)
        val notificationManager = AndroidNotificationManager(this)
        
        // Yêu cầu hệ thống cho phép ứng dụng vẽ tràn viền
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Ẩn thanh điều hướng ở dưới cùng
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        // (Tùy chọn) Cho phép người dùng vuốt từ mép dưới lên để hiển thị lại thanh này trong chốc lát mà không làm xô lệch giao diện
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            App(
                database = database,
                soundManager = soundManager,
                notificationManager = notificationManager
            )
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}