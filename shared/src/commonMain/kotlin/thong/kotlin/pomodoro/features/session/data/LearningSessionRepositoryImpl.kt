package thong.kotlin.pomodoro.features.session.data

import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.LearningSessionState

class LearningSessionRepositoryImpl(
    private val localDataSource: LocalLearningSessionDataSource
) : LearningSessionRepository {

    override fun getCurrentSession(): LearningSessionState {
        return localDataSource.getCurrentSession()
    }

    override fun saveCurrentSession(session: LearningSessionState) {
        localDataSource.saveCurrentSession(session)
    }

    override fun clearCurrentSession() {
        localDataSource.clearCurrentSession()
    }

    override fun saveCompletedSession(session: LearningSessionState) {
        // Tạm thời có thể chưa làm gì.
        // Sau này nên lưu vào database local hoặc server để thống kê.
        //
        // Ví dụ sau này:
        // completedSessionDao.insert(session)
    }
}