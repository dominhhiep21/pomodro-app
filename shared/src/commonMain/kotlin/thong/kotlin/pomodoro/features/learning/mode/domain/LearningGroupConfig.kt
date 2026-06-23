package thong.kotlin.pomodoro.features.learning.mode.domain

data class LearningGroupConfig(
    val maxGroupSize: Int = 4,
    val workMinutes: Int = 25,
    val breakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
)