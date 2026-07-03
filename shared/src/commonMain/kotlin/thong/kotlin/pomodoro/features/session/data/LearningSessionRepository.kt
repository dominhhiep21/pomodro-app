package thong.kotlin.pomodoro.features.session.data

import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.LearningSessionState
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.SessionTask
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEvent
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord

interface LearningSessionRepository {

    fun getCurrentSession(): LearningSessionState

    fun saveCurrentSession(session: LearningSessionState)

    fun clearCurrentSession()

    fun saveCompletedSession(session: LearningSessionState)

    fun getAllLearningSessionRecords(): List<LearningSessionRecord>

    fun getSessionById(sessionId: String): LearningSessionRecord?

    fun getTotalFocusSeconds(): Long

    fun insertSession(session: LearningSessionRecord)

    fun updateSession(session: LearningSessionRecord)

    fun deleteSessionById(sessionId: String)

    fun insertEvent(event: LearningSessionEvent)

    fun getAllTasksBySessionId(sessionId: String): List<SessionTask>

    fun insertTask(task: SessionTask, sessionId: String)

    fun updateTask(task: SessionTask)

    fun deleteTask(taskId: String, sessionId: String)

    fun clearAllSessionsData()
}