package thong.kotlin.pomodoro.features.pomodoro._base.domain.repository

import kotlinx.coroutines.flow.Flow
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettings
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.DailyStats
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.SessionRecord
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.SessionTask

interface UserAppStateRepository {
    // Settings
    fun getUserSettings(): UserSettings
    fun saveUserSettings(settings: UserSettings)
    fun getSettingsFlow(): Flow<UserSettings>

    // Tasks
    fun getAllTasks(): Flow<List<SessionTask>>
    suspend fun saveTask(sessionTask: SessionTask)
    suspend fun deleteTask(taskId: String)
    suspend fun updateTaskStatus(taskId: String, isCompleted: Boolean, completedAt: String?)

    // Sessions
    suspend fun saveSession(session: SessionRecord)
    fun getAllSessions(): Flow<List<SessionRecord>>

    // Statistics
    fun getTodayStats(): Flow<DailyStats?>
    suspend fun updateDailyStats(stats: DailyStats)
    suspend fun incrementDailyStats(
        sessionsCompleted: Int = 0,
        focusMinutes: Int = 0,
        breakMinutes: Int = 0,
        tasksCompleted: Int = 0
    )

    // Data Management
    suspend fun clearAllData()
}
