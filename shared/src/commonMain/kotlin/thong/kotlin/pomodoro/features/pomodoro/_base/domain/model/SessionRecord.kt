package thong.kotlin.pomodoro.features.pomodoro._base.domain.model

data class SessionRecord(
    val id: String,
    val startTime: String,
    val endTime: String?,
    val mode: String, // WORK, SHORT_BREAK, LONG_BREAK
    val durationMinutes: Int,
    val status: String, // COMPLETED, SKIPPED, CANCELLED
    val tasksCompletedCount: Int = 0
)
