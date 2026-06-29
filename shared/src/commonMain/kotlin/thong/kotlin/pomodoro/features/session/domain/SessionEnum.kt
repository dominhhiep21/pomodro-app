package thong.kotlin.pomodoro.features.session.domain

import androidx.compose.ui.graphics.Color
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors

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
    SESSION_CREATED,
    WORK_ROUND_PAUSED,
    WORK_ROUND_STARTED,
    WORK_ROUND_RESUMED,
    WORK_ROUND_COMPLETED,
    WORK_ROUND_RESET,
    WORK_ROUND_SKIPPED,
    BREAK_ROUND_STARTED,
    BREAK_ROUND_RESUMED,
    BREAK_ROUND_PAUSED,
    BREAK_ROUND_ENDED,
    BREAK_ROUND_RESET,
    BREAK_ROUND_SKIPPED,
    SESSION_STARTED,
    APP_EXITED,
    APP_WENT_BACKGROUND,
    USER_RETURNED_HOME,
    BACKGROUND_CHANGED,
    SOUND_MUTED,
    SOUND_UNMUTED,
    SESSION_COMPLETED_BY_USER
}

fun LearningSessionStatus.toDisplayText(): String =
    when (this) {
        LearningSessionStatus.IDLE -> "Chưa bắt đầu"
        LearningSessionStatus.RUNNING -> "Đang chạy"
        LearningSessionStatus.PAUSED -> "Tạm dừng"
        LearningSessionStatus.COMPLETED -> "Hoàn thành"
    }

fun CurrentLearningMode.toDisplayText(): String =
    when (this) {
        CurrentLearningMode.NOT_YET_STARTED -> "Chưa bắt đầu"
        CurrentLearningMode.WORK -> "Đang làm việc"
        CurrentLearningMode.BREAK -> "Nghỉ ngắn"
        CurrentLearningMode.LONG_BREAK -> "Nghỉ dài"
    }

fun LearningSessionStatus.color() : Color =
    when (this) {
        LearningSessionStatus.COMPLETED -> AuraColors.SessionCompletedMode
        LearningSessionStatus.RUNNING -> AuraColors.WorkMode
        LearningSessionStatus.PAUSED -> AuraColors.SessionPausedMode
        LearningSessionStatus.IDLE -> AuraColors.SessionIdleMode
    }