package thong.kotlin.pomodoro.features.session.data

import kotlinx.serialization.json.Json
import thong.kotlin.pomodoro.core.utils.toDateTimeText
import thong.kotlin.pomodoro.core.utils.toDateTimeTextOrNull
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.LearningSessionState
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.LocalSettingsDataSourceV2
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEvent
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.insertInto
import thong.kotlin.pomodoro.features.session.domain.toLearningSessionRecord
import kotlin.time.Clock

class LearningSessionRepositoryImpl(
    private val localDataSource: LocalSettingsDataSourceV2,
    private val database: AuraDatabase? = null
) : LearningSessionRepository {
    override fun getCurrentSession(): LearningSessionState {
        TODO("Not yet implemented")
    }

    override fun saveCurrentSession(session: LearningSessionState) {
        TODO("Not yet implemented")
    }

    override fun clearCurrentSession() {
        TODO("Not yet implemented")
    }

    override fun saveCompletedSession(session: LearningSessionState) {
        // Tạm thời có thể chưa làm gì.
        // Sau này nên lưu vào database local hoặc server để thống kê.
        //
        // Ví dụ sau này:
        // completedSessionDao.insert(session)
    }

    override fun getAllLearningSessionRecords(): List<LearningSessionRecord> {
        return database?.sessionHistoryLocalQueries
            ?.selectAllSessionHistory()
            ?.executeAsList()
            ?.map { row -> row.toLearningSessionRecord() } ?: emptyList()
    }

    override fun getSessionById(sessionId: String): LearningSessionRecord? {
        return database?.sessionHistoryLocalQueries
            ?.selectSessionHistoryById(sessionId)
            ?.executeAsOneOrNull()
            ?.toLearningSessionRecord()
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

            last_background_id = session.lastBackgroundId,
            last_music_id = session.lastMusicId,
            last_ambient_sound_json = Json.encodeToString(session.lastAmbientSounds),

            total_focus_seconds = session.totalFocusSeconds.toLong(),
            total_break_seconds = session.totalBreakSeconds.toLong(),
            total_paused_seconds = session.totalPausedSeconds.toLong(),

            created_at = session.createdAtMillis.toDateTimeText(),
            updated_at = session.updatedAtMillis.toDateTimeText(),

            sync_status = session.syncStatus.name
        )
    }

    override fun updateSession(session: LearningSessionRecord) {
        val queries = database?.sessionHistoryLocalQueries ?: return
        val existing = queries
            .selectSessionHistoryById(session.sessionId)
            .executeAsOneOrNull()

        val now = Clock.System.now().toEpochMilliseconds()

        if (existing == null) {
            insertSession(session)
        } else {
            queries.updateSessionHistory(
                user_id = session.userId,
                anonymous_user_id = session.anonymousUserId,
                session_mode = session.sessionMode.name,
                current_learning_mode = session.currentLearningMode.name,
                status = session.status.name,
                ended_at = session.endedAtMillis.toDateTimeTextOrNull(),
                last_paused_at = session.lastPausedAtMillis.toDateTimeTextOrNull(),
                planned_work_minutes = session.plannedWorkMinutes.toLong(),
                planned_break_minutes = session.plannedBreakMinutes.toLong(),
                completed_work_rounds = session.completedWorkRounds.toLong(),
                completed_break_rounds = session.completedBreakRounds.toLong(),
                last_background_id = session.lastBackgroundId,
                last_music_id = session.lastMusicId,
                last_ambient_sound_json = Json.encodeToString(session.lastAmbientSounds),
                total_focus_seconds = session.totalFocusSeconds.toLong(),
                total_break_seconds = session.totalBreakSeconds.toLong(),
                total_paused_seconds = session.totalPausedSeconds.toLong(),
                updated_at = now.toDateTimeText(),
                sync_status = session.syncStatus.name,
                id = session.sessionId
            )
        }
    }

    override fun deleteSessionById(sessionId: String) {
        database?.sessionHistoryLocalQueries?.deleteSessionHistoryById(sessionId)
    }

    override fun insertEvent(event: LearningSessionEvent) {
        val queries = database?.sessionHistoryLocalQueries ?: return
        event.insertInto(queries)
    }

    override fun clearAllSessionsData() {
        val queries = database?.sessionHistoryLocalQueries ?: return
        localDataSource.clearUserSettings()
        queries.deleteAllLearningSessionEvents()
        queries.deleteAllSessionHistory()
    }
}