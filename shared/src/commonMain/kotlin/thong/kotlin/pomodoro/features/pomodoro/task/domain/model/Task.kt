package thong.kotlin.pomodoro.features.pomodoro.task.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Task(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false,
    val createdAt: String = "",
    val completedAt: String? = null,
    val sessionId: String? = null
)
