package thong.kotlin.pomodoro.features.streak.domain

import kotlinx.coroutines.flow.Flow
import thong.kotlin.pomodoro.features.streak.domain.model.DailyRecord

interface StreakRepository {
    fun getHistoryFlow(): Flow<List<DailyRecord>>
    suspend fun getHistory(): List<DailyRecord>
    suspend fun recordDay(date: String, sessionsCompleted: Int)
    suspend fun incrementToday()
}
