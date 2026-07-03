package thong.kotlin.pomodoro.features.streak.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import thong.kotlin.pomodoro.features.streak.domain.StreakRepository
import thong.kotlin.pomodoro.features.streak.domain.model.DailyRecord

class StreakRepositoryImpl(private val settings: Settings) : StreakRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val _historyFlow = MutableStateFlow(loadHistory())

    companion object {
        private const val KEY_STREAK_HISTORY = "streak_history_json"
        private const val MAX_DAYS = 365
    }

    override fun getHistoryFlow(): Flow<List<DailyRecord>> = _historyFlow.asStateFlow()

    override suspend fun getHistory(): List<DailyRecord> = _historyFlow.value

    override suspend fun recordDay(date: String, sessionsCompleted: Int) {
        val history = loadHistory().toMutableList()
        val existing = history.indexOfFirst { it.date == date }
        if (existing >= 0) {
            history[existing] = DailyRecord(date, sessionsCompleted)
        } else {
            history.add(DailyRecord(date, sessionsCompleted))
        }
        val trimmed = history.sortedByDescending { it.date }.take(MAX_DAYS)
        saveHistory(trimmed)
        _historyFlow.value = trimmed
    }

    override suspend fun incrementToday() {
        val today = getCurrentDate()
        val history = loadHistory()
        val current = history.find { it.date == today }
        val newCount = (current?.sessionsCompleted ?: 0) + 1
        recordDay(today, newCount)
    }

    private fun loadHistory(): List<DailyRecord> {
        val jsonStr = settings.getStringOrNull(KEY_STREAK_HISTORY) ?: return emptyList()
        return try {
            json.decodeFromString<List<DailyRecord>>(jsonStr)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveHistory(history: List<DailyRecord>) {
        settings[KEY_STREAK_HISTORY] = json.encodeToString(history)
    }
}

internal expect fun getCurrentDate(): String
