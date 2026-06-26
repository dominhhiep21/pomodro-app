package thong.kotlin.pomodoro.features.session.data

import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.LearningSessionState

interface LearningSessionRepository {

    fun getCurrentSession(): LearningSessionState

    fun saveCurrentSession(session: LearningSessionState)

    fun clearCurrentSession()

    fun saveCompletedSession(session: LearningSessionState)

}