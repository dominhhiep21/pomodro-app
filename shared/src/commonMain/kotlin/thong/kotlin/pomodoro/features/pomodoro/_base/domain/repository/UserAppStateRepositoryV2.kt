package thong.kotlin.pomodoro.features.pomodoro._base.domain.repository

import kotlinx.coroutines.flow.Flow
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettingsV2

interface UserAppStateRepositoryV2 {

    fun getUserSettings(): UserSettingsV2

    fun getSettingsFlow(): Flow<UserSettingsV2>

    fun saveUserSettings(settings: UserSettingsV2)

    fun updateUserSettings(transform: (UserSettingsV2) -> UserSettingsV2)

    fun markOnboardingCompleted()

    fun updatePersonalPomodoroTime(workMinutes: Int, breakMinutes: Int, longBreakMinutes: Int)

    fun updateAutoStartSettings(autoStartBreak: Boolean, autoStartWork: Boolean)

    fun updateSelectedBackground(backgroundId: String?)

    fun updateDailyTarget(minutes: Int)

    fun updateNotificationSound(enabled: Boolean)

    fun updateVibration(enabled: Boolean)

    fun updateFirstDayOfWeek(day: Int)

    fun updateLanguage(lang: String)

    fun updateDarkMode(enabled: Boolean)

    fun resetUserSettings()
//
//    // Tasks
//    fun getAllTasks(): Flow<List<Task>>
//    suspend fun saveTask(task: Task)
//    suspend fun deleteTask(taskId: String)
//    suspend fun updateTaskStatus(taskId: String, isCompleted: Boolean, completedAt: String?)
//
//    // Sessions
//    suspend fun saveSession(session: SessionRecord)
//    fun getAllSessions(): Flow<List<SessionRecord>>
//
//    // Statistics
//    fun getTodayStats(): Flow<DailyStats?>
//    suspend fun updateDailyStats(stats: DailyStats)
//    suspend fun incrementDailyStats(
//        sessionsCompleted: Int = 0,
//        focusMinutes: Int = 0,
//        breakMinutes: Int = 0,
//        tasksCompleted: Int = 0
//    )
//
//    // Data Management
//    suspend fun clearAllData()
}