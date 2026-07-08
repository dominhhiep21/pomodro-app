package thong.kotlin.pomodoro.core.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AuraGradients {
    // Gradient nền mờ ảo cho màn hình khởi động (Splash) hoặc các góc phòng học
    val AmbientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1E112A), // Tím thẫm huyền bí
            Color(0xFF09090B)  // Đen sâu hệ thống
        )
    )

    // Gradient viền kính phản chiếu ánh sáng (Dùng cho border của GlassBox để tạo nét sắc sảo)
    val GlassBorder = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.18f),
            Color.White.copy(alpha = 0.02f),
            Color.White.copy(alpha = 0.10f)
        )
    )

    // Gradient đại diện cho dòng chảy năng lượng khi Tập trung (Work Mode)
    val WorkFlow = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFF43F5E), // Rose 500
            Color(0xFFFB7185)  // Rose 400 (Sáng hơn ở rìa)
        )
    )

    // Gradient mang lại sự bình yên khi Nghỉ ngơi (Break Mode)
    val BreakFlow = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF00BBA1), // Teal 500
            Color(0xFF51FFEB)  // Teal 400
        )
    )

    // Gradient mang lại sự thư giãn sâu khi Nghỉ dài (Long Break Mode)
    val LongBreakFlow = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF3B82F6), // Blue 500
            Color(0xFF60A5FA)  // Blue 400
        )
    )
}