package thong.kotlin.pomodoro.features.streak.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyRecord(
    val date: String,            // YYYY-MM-DD
    val sessionsCompleted: Int = 0
)
