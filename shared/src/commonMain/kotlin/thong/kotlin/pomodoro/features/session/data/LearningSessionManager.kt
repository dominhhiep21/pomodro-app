package thong.kotlin.pomodoro.features.session.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.SessionTask
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEvent
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import kotlin.time.Clock

class LearningSessionManager(
    private val repository: LearningSessionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getAllLearningSession(): List<LearningSessionRecord> {
        return withContext(ioDispatcher) {
            repository.getAllLearningSessionRecords()
        }
    }

    suspend fun getSessionById(sessionId: String): LearningSessionRecord? {
        return withContext(ioDispatcher) {
            repository.getSessionById(sessionId)
        }
    }
    suspend fun getTotalFocusSeconds(): Long {
        return withContext(ioDispatcher) {
            repository.getTotalFocusSeconds()
        }
    }

    suspend fun insertSession(session: LearningSessionRecord) {
        withContext(ioDispatcher) {
            repository.insertSession(session)
        }
    }

    suspend fun updateSession(session: LearningSessionRecord) {
        withContext(ioDispatcher) {
            repository.updateSession(session)
        }
    }

    suspend fun deleteSessionById(sessionId: String) {
        withContext(ioDispatcher) {
            repository.deleteSessionById(sessionId)
        }
    }

    suspend fun insertEvent(event: LearningSessionEvent) {
        withContext(ioDispatcher) {
            repository.insertEvent(event)
        }
    }

    suspend fun getAllTasksBySessionId(sessionId: String): List<SessionTask> {
        return withContext(ioDispatcher) {
            repository.getAllTasksBySessionId(sessionId)
        }
    }

    suspend fun insertTask(task: SessionTask, sessionId: String) {
        withContext(ioDispatcher) {
            repository.insertTask(task, sessionId)
        }
    }

    suspend fun updateTask(task: SessionTask) {
        withContext(ioDispatcher) {
            repository.updateTask(task)
        }
    }

    suspend fun deleteTaskById(taskId: String, sessionId: String) {
        withContext(ioDispatcher) {
            repository.deleteTask(taskId, sessionId)
        }
    }

    suspend fun clearAllSessionsData() {
        withContext(ioDispatcher) {
            repository.clearAllSessionsData()
        }
    }

    private fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}