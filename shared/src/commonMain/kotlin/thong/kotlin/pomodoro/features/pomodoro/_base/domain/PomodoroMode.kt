package thong.kotlin.pomodoro.features.pomodoro._base.domain

enum class PomodoroMode(val label: String) {
    WORK("Tập trung"),        // 25 phút
    SHORT_BREAK("Nghỉ ngắn"),  // 1 phút
    LONG_BREAK("Nghỉ dài")    // 15 phút
}

fun PomodoroMode.totalSeconds(config: PomodoroConfig): Long {
    return when (this) {
        PomodoroMode.WORK -> config.workSeconds.toLong()
        PomodoroMode.SHORT_BREAK -> config.shortBreakSeconds.toLong()
        PomodoroMode.LONG_BREAK -> config.longBreakSeconds.toLong()
    }
}