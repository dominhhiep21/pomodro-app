package thong.kotlin.pomodoro.core.designsystem.theme

import androidx.compose.ui.graphics.Color

object AuraColors {
    val Background = Color(0xFF09090B)          // Nền đen sâu (Slate 950)
    val TextPrimary = Color(0xFFFFFFFF)         // Chữ trắng chính
    val TextSecondary = Color(0.9f, 0.9f, 0.9f, 0.6f) // Chữ mờ 60%

    // Màu sắc đại diện cho các chế độ Pomodoro
    val MainAppMode = Color(0xFFFFFFFF)
    val WorkMode = Color(0xFFF43F5E)            // Hồng Rose (Tập trung)
    val ShortBreakMode = Color(0xFF14B8A6)      // Xanh Teal (Nghỉ ngắn)
    val LongBreakMode = Color(0xFF3B82F6)       // Xanh Dương (Nghỉ dài)

    // Màu nền cho các thanh công cụ (Bottom Bar)
    val BottomBarBackground = Color(0xFF18181B) // Nền đen Zinc (Opaque)

    // Additional tokens used by streak feature
    val surface = Color(0xFF18181B)             // Surface / elevated background
    val primary = WorkMode                      // Primary accent (rose)
}