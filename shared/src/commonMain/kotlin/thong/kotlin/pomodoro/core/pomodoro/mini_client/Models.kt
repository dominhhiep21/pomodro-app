package thong.kotlin.pomodoro.core.pomodoro.mini_client

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterRequest(
    val email: String,
    val username: String,
    val password: String
)

@Serializable
data class UserLoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class SettingsRequest(
    val pomodoroLength: Int,
    val shortBreakLength: Int,
    val longBreakLength: Int,
    val longBreakInterval: Int
)

@Serializable
data class PomodoroLogRequest(
    val taskId: String? = null,
    val durationSeconds: Long,
    val status: String, // "COMPLETED" hoặc "ABANDONED"
    val startedAt: String,
    val endedAt: String
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val username: String,
    val createdAt: String
)

@Serializable
data class ErrorResponse(
    val error: String
)
