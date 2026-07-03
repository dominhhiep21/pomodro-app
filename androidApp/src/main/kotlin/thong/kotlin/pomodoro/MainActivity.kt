package thong.kotlin.pomodoro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import thong.kotlin.pomodoro.core.media.AndroidSoundManager
import thong.kotlin.pomodoro.core.notification.AndroidNotificationManager
import thong.kotlin.pomodoro.database.DatabaseDriverFactory
import thong.kotlin.pomodoro.di.DependencyRegistry

@Composable
fun RequestNotificationPermissionEffect() {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Có thể lưu trạng thái nếu cần
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Khởi tạo Database context
        val database = DatabaseDriverFactory(this).createDriver()
        val soundManager = AndroidSoundManager.getInstance(this)
        val notificationManager = AndroidNotificationManager(this)

        DependencyRegistry.initDatabase(database)
        DependencyRegistry.initSoundManager(soundManager)
        DependencyRegistry.initNotificationManager(notificationManager)

        // Yêu cầu hệ thống cho phép ứng dụng vẽ tràn viền
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Ẩn thanh điều hướng ở dưới cùng
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        // (Tùy chọn) Cho phép người dùng vuốt từ mép dưới lên để hiển thị lại thanh này trong chốc lát mà không làm xô lệch giao diện
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            // Yêu cầu quyền thông báo trên Android 13+
            RequestNotificationPermissionEffect()
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}