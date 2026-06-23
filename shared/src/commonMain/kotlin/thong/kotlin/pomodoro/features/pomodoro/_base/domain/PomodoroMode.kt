package thong.kotlin.pomodoro.features.pomodoro._base.domain

enum class PomodoroMode(val label: String) {
    WORK("Tập trung"),        // 25 phút
    SHORT_BREAK("Nghỉ ngắn"),  // 1 phút
    LONG_BREAK("Nghỉ dài")    // 15 phút
}