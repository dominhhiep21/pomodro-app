package thong.kotlin.pomodoro.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring

object AuraAnimations {
    // Đường cong chuyển động tùy chỉnh: Nhanh ở đầu, mượt ở cuối (Eased Out)
    val SmoothEasing = CubicBezierEasing(0.25f, 1.0f, 0.5f, 1.0f)

    // Cấu hình chuyển màu động mặc định cho toàn app (Thời gian 450ms)
    fun <T> colorTween() = TweenSpec<T>(
        durationMillis = 450,
        easing = SmoothEasing
    )

    // Hiệu ứng lò xo co giãn vật lý cao cấp dành cho nút bấm bấm lún hoặc kéo thả task
    val SnappySpring = spring<Float>(
        dampingRatio = 0.65f, // Tạo độ nảy nhẹ (Bouncy) ở điểm dừng
        stiffness = 300f      // Tốc độ phản hồi nhanh, dứt khoát
    )
}