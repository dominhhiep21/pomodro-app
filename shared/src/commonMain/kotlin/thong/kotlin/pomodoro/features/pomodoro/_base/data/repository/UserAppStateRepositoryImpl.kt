package thong.kotlin.pomodoro.features.pomodoro._base.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableStateFlow
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettings
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.DailyStats
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.SessionRecord
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.SessionTask
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepository
import thong.kotlin.pomodoro.features.pomodoro._base.data.local.LocalSettingsDataSource
import thong.kotlin.pomodoro.core.utils.getCurrentDateString

class UserAppStateRepositoryImpl(
    private val localSettings: LocalSettingsDataSource
) : UserAppStateRepository {

    private val _tasksFlow = MutableStateFlow(localSettings.getTasks())
    private val _statsFlow = MutableStateFlow(localSettings.getDailyStats(getCurrentDateString()))

    override fun getUserSettings(): UserSettings = localSettings.getUserSettings()

    override fun saveUserSettings(settings: UserSettings) {
        localSettings.saveUserSettings(settings)
    }

    override fun getSettingsFlow(): Flow<UserSettings> = localSettings.getSettingsFlow()

    override fun getAllTasks(): Flow<List<SessionTask>> = _tasksFlow

    override suspend fun saveTask(sessionTask: SessionTask) {
        val currentTasks = localSettings.getTasks().toMutableList()
        val index = currentTasks.indexOfFirst { it.taskId == sessionTask.taskId }
        if (index != -1) {
            currentTasks[index] = sessionTask
        } else {
            currentTasks.add(0, sessionTask)
        }
        localSettings.saveTasks(currentTasks)
        _tasksFlow.value = currentTasks
    }

    override suspend fun deleteTask(taskId: String) {
        val currentTasks = localSettings.getTasks().filter { it.taskId != taskId }
        localSettings.saveTasks(currentTasks)
        _tasksFlow.value = currentTasks
    }

    override suspend fun updateTaskStatus(taskId: String, isCompleted: Boolean, completedAt: String?) {
        val currentTasks = localSettings.getTasks().map {
            if (it.taskId == taskId) it.copy(isCompleted = isCompleted) else it
        }
        localSettings.saveTasks(currentTasks)
        _tasksFlow.value = currentTasks
    }

    override suspend fun saveSession(session: SessionRecord) {
        // Session history not persisted in Settings-only version to avoid large blobs
    }

    override fun getAllSessions(): Flow<List<SessionRecord>> = flow { emit(emptyList()) }

    override fun getTodayStats(): Flow<DailyStats?> = _statsFlow

    override suspend fun updateDailyStats(stats: DailyStats) {
        localSettings.saveDailyStats(stats)
        _statsFlow.value = stats
    }

    override suspend fun incrementDailyStats(
        sessionsCompleted: Int,
        focusMinutes: Int,
        breakMinutes: Int,
        tasksCompleted: Int
    ) {
        val today = getCurrentDateString()
        val current = localSettings.getDailyStats(today) ?: DailyStats(date = today)
        val updated = current.copy(
            sessionsCompleted = current.sessionsCompleted + sessionsCompleted,
            focusMinutes = current.focusMinutes + focusMinutes,
            breakMinutes = current.breakMinutes + breakMinutes,
            tasksCompleted = current.tasksCompleted + tasksCompleted
        )
        updateDailyStats(updated)
    }

    override suspend fun clearAllData() {
        localSettings.clear()
        _tasksFlow.value = emptyList()
        _statsFlow.value = null
    }
}
