package thong.kotlin.pomodoro.features.session.data

import thong.kotlin.pomodoro.core.utils.toDateTimeText
import thong.kotlin.pomodoro.core.utils.toDateTimeTextOrNull
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.LearningSessionState
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.toLearningSessionRecord

class LearningSessionRepositoryImpl(
    private val localDataSource: LocalLearningSessionDataSource,
    private val database: AuraDatabase? = null
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

    override fun getAllLearningSessionRecords(): List<LearningSessionRecord> {
        return database?.sessionHistoryLocalQueries?.selectAllSessionHistory()?.executeAsList()
            ?.map { row -> row.toLearningSessionRecord() } ?: emptyList()
    }


    override fun getTotalFocusSeconds() =
        database?.sessionHistoryLocalQueries?.selectTotalFocusSeconds()?.executeAsOne()?.toLong()
            ?: 0

    override fun insertSession(session: LearningSessionRecord) {
        database?.sessionHistoryLocalQueries?.insertSessionHistory(
            id = session.sessionId,
            user_id = session.userId,
            anonymous_user_id = session.anonymousUserId,
            session_mode = session.sessionMode.name,
            status = session.status.name,
            current_learning_mode = session.currentLearningMode.name,

            started_at = session.startedAtMillis.toDateTimeText(),
            ended_at = session.endedAtMillis.toDateTimeTextOrNull(),
            last_paused_at = session.lastPausedAtMillis.toDateTimeTextOrNull(),

            planned_work_minutes = session.plannedWorkMinutes.toLong(),
            planned_break_minutes = session.plannedBreakMinutes.toLong(),
            completed_work_rounds = session.completedWorkRounds.toLong(),
            completed_break_rounds = session.completedBreakRounds.toLong(),

            total_focus_seconds = session.totalFocusSeconds.toLong(),
            total_break_seconds = session.totalBreakSeconds.toLong(),
            total_paused_seconds = session.totalPausedSeconds.toLong(),

            created_at = session.createdAtMillis.toDateTimeText(),
            updated_at = session.updatedAtMillis.toDateTimeText(),

            sync_status = session.syncStatus.name
        )
    }
}