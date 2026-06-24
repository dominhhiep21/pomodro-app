package thong.kotlin.pomodoro.features.learning.mode.domain

data class LearningGroupConfig(
    val maxGroupSize: Int = 4,
    val workMinutes: Int = 25,
    val breakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
)

enum class LearningStyle {
    SOLO, GROUP
}

data class ChatMessage(
    val sender: String,
    val text: String,
    val isMe: Boolean
)

enum class ExpandDirection {
    TO_LEFT,  // Nút ở bên phải, mở rộng sang trái
    TO_RIGHT  // Nút ở bên trái, mở rộng sang phải
}