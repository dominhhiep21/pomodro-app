package thong.kotlin.pomodoro.features.startup.domain

import thong.kotlin.pomodoro.database.GetHomeDashboardStats

data class HomeUiDomain(
    val todayFocusSeconds : Int = 0,
    val focusSecondsDiff: Int = 0,
    val todayCompletedPomodoros : Int = 0,
    val completedPomodorosDiff: Int = 0,
    val currentStreakDays : Int = 0,
    val bestStreakDays : Int = 0
)

fun GetHomeDashboardStats.toHomeUiDomain(): HomeUiDomain {
    return HomeUiDomain(
        todayFocusSeconds = today_focus_seconds.toInt(),
        focusSecondsDiff = focus_seconds_diff.toInt(),
        todayCompletedPomodoros = today_completed_pomodoros.toInt(),
        completedPomodorosDiff = completed_pomodoros_diff.toInt(),
        currentStreakDays = current_streak_days.toInt(),
        bestStreakDays = best_streak_days.toInt()
    )
}