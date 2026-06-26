package thong.kotlin.pomodoro.features.streak.domain

import thong.kotlin.pomodoro.features.streak.domain.model.DailyRecord
import thong.kotlin.pomodoro.features.streak.domain.model.StreakData

object StreakCalculator {

    fun calculate(history: List<DailyRecord>, today: String): StreakData {
        if (history.isEmpty()) return StreakData()

        val activeDays = history
            .filter { it.sessionsCompleted > 0 }
            .map { it.date }
            .toSortedSet()

        if (activeDays.isEmpty()) return StreakData(history = history)

        val currentStreak = calculateCurrentStreak(activeDays, today)
        val longestStreak = calculateLongestStreak(activeDays)

        return StreakData(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            history = history
        )
    }

    private fun calculateCurrentStreak(activeDays: Set<String>, today: String): Int {
        var streak = 0
        var checkDate = today

        // Check today first, then go backwards
        while (checkDate in activeDays) {
            streak++
            checkDate = previousDay(checkDate)
        }

        // If today has no activity yet, check starting from yesterday
        if (streak == 0) {
            checkDate = previousDay(today)
            while (checkDate in activeDays) {
                streak++
                checkDate = previousDay(checkDate)
            }
        }

        return streak
    }

    private fun calculateLongestStreak(activeDays: Set<String>): Int {
        if (activeDays.isEmpty()) return 0

        val sorted = activeDays.sorted()
        var longest = 1
        var current = 1

        for (i in 1 until sorted.size) {
            if (sorted[i] == nextDay(sorted[i - 1])) {
                current++
                if (current > longest) longest = current
            } else {
                current = 1
            }
        }
        return longest
    }

    private fun previousDay(date: String): String = offsetDay(date, -1)
    private fun nextDay(date: String): String = offsetDay(date, 1)

    private fun offsetDay(date: String, offset: Int): String {
        // Parse YYYY-MM-DD manually for multiplatform compatibility
        val parts = date.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()

        val daysInMonth = daysInMonth(year, month)
        var newDay = day + offset
        var newMonth = month
        var newYear = year

        if (newDay > daysInMonth) {
            newDay = 1; newMonth++
            if (newMonth > 12) { newMonth = 1; newYear++ }
        } else if (newDay < 1) {
            newMonth--
            if (newMonth < 1) { newMonth = 12; newYear-- }
            newDay = daysInMonth(newYear, newMonth)
        }

        return "${newYear}-${newMonth.toString().padStart(2, '0')}-${newDay.toString().padStart(2, '0')}"
    }

    private fun daysInMonth(year: Int, month: Int): Int = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }
}
