package thong.kotlin.pomodoro.features.session.domain

import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle

data class LearningSessionHistory(
    val sessionId: String,
    val userId: String?,
    val anonymousUserId: String?,
    val mode: LearningStyle,
    val status: LearningSessionStatus,
    val startedAtMillis: Long,
    val endedAtMillis: Long,
    val plannedWorkMinutes: Long,
    val plannedBreakMinutes: Long,
    val plannedLongBreakMinutes: Long,
    val totalFocusSeconds: Long,
    val totalBreakSeconds: Long,
    val totalPausedSeconds: Long,
    val completedWorkRounds: Long,
    val backgroundId: String?,
    val groupRoomId: String?,
    val eventLogJson: String?,
    val syncStatus: SyncStatus,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)