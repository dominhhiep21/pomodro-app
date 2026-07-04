package thong.kotlin.pomodoro.features.session.data

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import thong.kotlin.pomodoro.core.utils.toDateTimeText
import thong.kotlin.pomodoro.core.utils.toDateTimeTextOrNull
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.LearningSessionState
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.LocalSettingsDataSourceV2
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.SessionTask
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.toSessionTaskRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEvent
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.insertInto
import thong.kotlin.pomodoro.features.session.domain.toLearningSessionRecord
import thong.kotlin.pomodoro.features.startup.domain.HomeUiDomain
import thong.kotlin.pomodoro.features.startup.domain.toHomeUiDomain
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
            started_at_millis = session.startedAtMillis,
            ended_at = session.endedAtMillis.toDateTimeTextOrNull(),
            ended_at_millis = session.endedAtMillis,
            last_paused_at = session.lastPausedAtMillis.toDateTimeTextOrNull(),
            last_paused_at_millis = session.lastPausedAtMillis,

            planned_work_minutes = session.plannedWorkMinutes.toLong(),
            planned_break_minutes = session.plannedBreakMinutes.toLong(),
            planned_long_break_minutes = session.plannedLongBreakMinutes.toLong(),
            completed_work_rounds = session.completedWorkRounds.toLong(),
            completed_break_rounds = session.completedBreakRounds.toLong(),

            last_background_id = session.lastBackgroundId,
            last_music_id = session.lastMusicId,
            last_ambient_sound_json = Json.encodeToString(session.lastAmbientSounds),

            total_focus_seconds = session.totalFocusSeconds.toLong(),
            total_break_seconds = session.totalBreakSeconds.toLong(),
            total_paused_seconds = session.totalPausedSeconds.toLong(),

            created_at = session.createdAtMillis.toDateTimeText(),
            created_at_millis = session.createdAtMillis,
            updated_at = session.updatedAtMillis.toDateTimeText(),
            updated_at_millis = session.updatedAtMillis,

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

                started_at = session.startedAtMillis.toDateTimeText(),
                started_at_millis = session.startedAtMillis,
                ended_at = session.endedAtMillis.toDateTimeTextOrNull(),
                ended_at_millis = session.endedAtMillis,
                last_paused_at = session.lastPausedAtMillis.toDateTimeTextOrNull(),
                last_paused_at_millis = session.lastPausedAtMillis,

                planned_work_minutes = session.plannedWorkMinutes.toLong(),
                planned_break_minutes = session.plannedBreakMinutes.toLong(),
                planned_long_break_minutes = session.plannedLongBreakMinutes.toLong(),
                completed_work_rounds = session.completedWorkRounds.toLong(),
                completed_break_rounds = session.completedBreakRounds.toLong(),

                last_background_id = session.lastBackgroundId,
                last_music_id = session.lastMusicId,
                last_ambient_sound_json = Json.encodeToString(session.lastAmbientSounds),
                total_focus_seconds = session.totalFocusSeconds.toLong(),
                total_break_seconds = session.totalBreakSeconds.toLong(),
                total_paused_seconds = session.totalPausedSeconds.toLong(),

                created_at = session.createdAtMillis.toDateTimeText(),
                created_at_millis = session.createdAtMillis,
                updated_at = now.toDateTimeText(),
                updated_at_millis = now,

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

    override fun getAllTasksBySessionId(sessionId: String): List<SessionTask> {
        val queries = database?.sessionHistoryLocalQueries ?: return emptyList()
        return queries.selectTasksBySessionId(sessionId).executeAsList()
            .map { row -> row.toSessionTaskRecord() }
    }

    override fun insertTask(task: SessionTask, sessionId: String) {
        val queries = database?.sessionHistoryLocalQueries ?: return
        queries.insertSessionTask(
            task_id = task.taskId,
            session_id = sessionId,
            title = task.title,
            is_completed = if (task.isCompleted) 1 else 0,
            focus_seconds = task.focusSeconds,
            created_at = task.createdAtMillis.toDateTimeText(),
            updated_at = task.updatedAtMillis.toDateTimeText(),
            completed_at = task.completedAtMillis?.toDateTimeText(),
            sync_status = task.syncStatus.name
        )
    }

    override fun updateTask(task: SessionTask) {
        val queries = database?.sessionHistoryLocalQueries ?: return
        queries.updateTask(
            task_id = task.taskId,
            session_id = task.sessionId,
            title = task.title,
            is_completed = if (task.isCompleted) 1 else 0,
            focus_seconds = task.focusSeconds,
            updated_at = task.updatedAtMillis.toDateTimeText(),
            completed_at = task.completedAtMillis?.toDateTimeText(),
            sync_status = task.syncStatus.name
        )
    }

    override fun deleteTask(taskId: String, sessionId: String) {
        val queries = database?.sessionHistoryLocalQueries ?: return
        queries.deleteTask(taskId, sessionId)
    }

    override fun clearAllSessionsData() {
        val queries = database?.sessionHistoryLocalQueries ?: return
        localDataSource.clearUserSettings()
        queries.deleteAllLearningSessionEvents()
        queries.deleteAllSessionHistory()
    }

    override fun getHomeDashboardStats(): HomeUiDomain {
        val queries = database?.sessionHistoryLocalQueries ?: return HomeUiDomain()
        val timeZone = TimeZone.currentSystemDefault()
        val todayDate = Clock.System.now().toLocalDateTime(timeZone).date
        val todayStartMillis = todayDate.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val tomorrowStartMillis = todayDate.plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(timeZone).toEpochMilliseconds()
        val yesterdayStartMillis = todayDate.minus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(timeZone).toEpochMilliseconds()

        return queries.getHomeDashboardStats(
            yesterday_start_millis = yesterdayStartMillis,
            today_start_millis = todayStartMillis,
            tomorrow_start_millis = tomorrowStartMillis
        ).executeAsOne().toHomeUiDomain()
    }
}