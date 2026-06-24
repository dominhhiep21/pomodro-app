package thong.kotlin.pomodoro.features.pomodoro._base.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyStats(
    val date: String, // YYYY-MM-DD
    val sessionsCompleted: Int = 0,
    val focusMinutes: Int = 0,
    val breakMinutes: Int = 0,
    val tasksCompleted: Int = 0
)
