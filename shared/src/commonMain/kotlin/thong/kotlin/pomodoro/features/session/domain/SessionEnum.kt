package thong.kotlin.pomodoro.features.session.domain

enum class LearningSessionStatus {
    IDLE,       // Chưa có session nào
    RUNNING,   // Đang học
    PAUSED,    // Tạm dừng
    COMPLETED  // Đã kết thúc chính thức
}

enum class CurrentLearningMode {
    NOT_YET_STARTED,
    WORK,
    BREAK,
    LONG_BREAK
}

enum class SyncStatus {
    LOCAL_ONLY,
    PENDING_SYNC,
    SYNCING,
    SYNCED,
    SYNC_FAILED
}

enum class LearningSessionEventType {
    SESSION_STARTED,
    TIMER_STARTED,
    TIMER_PAUSED,
    TIMER_RESUMED,
    APP_EXITED,
    APP_WENT_BACKGROUND,
    USER_RETURNED_HOME,
    WORK_ROUND_STARTED,
    WORK_ROUND_COMPLETED,
    BREAK_STARTED,
    BREAK_COMPLETED,
    LONG_BREAK_STARTED,
    LONG_BREAK_COMPLETED,
    USER_SKIPPED_BREAK,
    USER_SKIPPED_WORK,
    BACKGROUND_CHANGED,
    SOUND_MUTED,
    SOUND_UNMUTED,
    SESSION_COMPLETED_BY_USER
}