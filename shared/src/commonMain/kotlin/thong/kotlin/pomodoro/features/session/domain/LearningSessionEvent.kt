package thong.kotlin.pomodoro.features.session.domain

data class LearningSessionEvent(
    val eventId: String,
    val sessionId: String,
    val eventType: LearningSessionEventType,
    val occurredAtMillis: Long,
    val remainingSeconds: Int?,
    val currentRound: Int?,
    val metadata: Map<String, String> = emptyMap(),
    val syncStatus: SyncStatus
)

data class LearningSessionEventLog(
    val type: String,
    val time: Long,
    val remainingSeconds: Long? = null,
    val currentRound: Long? = null
)