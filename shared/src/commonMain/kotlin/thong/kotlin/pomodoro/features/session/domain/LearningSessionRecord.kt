package thong.kotlin.pomodoro.features.session.domain

import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle

data class LearningSessionRecord(
    val sessionId: String,
    val userId: String? = null,
    val anonymousUserId: String? = null,
    val sessionMode: LearningStyle,
    val status: LearningSessionStatus,
    val startedAtMillis: Long,
    val endedAtMillis: Long?,
    val lastPausedAtMillis: Long?,
    val plannedWorkMinutes: Int,
    val plannedBreakMinutes: Int,
    val plannedLongBreakMinutes: Int,
    val totalFocusSeconds: Int,
    val totalBreakSeconds: Int,
    val totalPausedSeconds: Int,
    val completedWorkRounds: Int,
    val completedBreakRounds: Int,
    val backgroundId: String?,
    val groupRoomId: String? = null,
    val groupSessionId: String? = null,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val syncStatus: SyncStatus
)