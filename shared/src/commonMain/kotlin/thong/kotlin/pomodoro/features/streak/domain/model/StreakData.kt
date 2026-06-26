package thong.kotlin.pomodoro.features.streak.domain.model

data class StreakData(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val history: List<DailyRecord> = emptyList()
)
