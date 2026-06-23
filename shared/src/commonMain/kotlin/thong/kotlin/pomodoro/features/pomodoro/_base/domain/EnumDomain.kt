package thong.kotlin.pomodoro.features.pomodoro._base.domain

enum class EventType {
    CLICK_START_WORK,
    CLICK_PAUSE_WORK,
    WORK_END,
    CLICK_START_BREAK,
    CLICK_PAUSE_BREAK,
    BREAK_END,
    NOTHING
}

enum class CompactSection {
    TASKS, MUSIC, BACKGROUND, AMBIENT, SETTINGS
}