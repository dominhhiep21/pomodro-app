package thong.kotlin.pomodoro.features.session.domain

import thong.kotlin.pomodoro.core.config.AppConfig
import thong.kotlin.pomodoro.core.utils.toDateTimeText
import thong.kotlin.pomodoro.core.utils.toDateTimeTextOrNull
import thong.kotlin.pomodoro.core.utils.toEnumOrDefault
import thong.kotlin.pomodoro.core.utils.toMillisFromDateTimeText
import thong.kotlin.pomodoro.core.utils.toMillisFromDateTimeTextOrNull
import thong.kotlin.pomodoro.database.SessionHistoryLocalQueries
import thong.kotlin.pomodoro.database.Session_history_local
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import kotlin.time.Clock

data class LearningSessionRecord(
    val sessionId: String,
    val userId: String? = null,
    val anonymousUserId: String? = null,
    val sessionMode: LearningStyle = LearningStyle.SOLO,
    val status: LearningSessionStatus = LearningSessionStatus.IDLE,
    val currentLearningMode : CurrentLearningMode = CurrentLearningMode.NOT_YET_STARTED,
    val startedAtMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val endedAtMillis: Long? = null,
    val lastPausedAtMillis: Long? = null,
    val plannedWorkMinutes: Int = AppConfig.DEFAULT_WORK_MINUTES,
    val plannedBreakMinutes: Int = AppConfig.DEFAULT_BREAK_MINUTES,
    val plannedLongBreakMinutes: Int = AppConfig.DEFAULT_LONG_BREAK_MINUTES,
    val totalFocusSeconds: Int = 0,
    val totalBreakSeconds: Int = 0,
    val totalPausedSeconds: Int = 0,
    val completedWorkRounds: Int = 0,
    val completedBreakRounds: Int = 0,
    val createdAtMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAtMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY
)

fun Session_history_local.toLearningSessionRecord(): LearningSessionRecord {
    return LearningSessionRecord(
        sessionId = id,
        userId = user_id,
        anonymousUserId = anonymous_user_id,
        sessionMode = session_mode.toEnumOrDefault(LearningStyle.SOLO),
        status = status.toEnumOrDefault(LearningSessionStatus.IDLE),
        currentLearningMode = current_learning_mode.toEnumOrDefault(CurrentLearningMode.NOT_YET_STARTED),

        startedAtMillis = started_at.toMillisFromDateTimeText(),
        endedAtMillis = ended_at.toMillisFromDateTimeTextOrNull(),
        lastPausedAtMillis = last_paused_at.toMillisFromDateTimeTextOrNull(),

        plannedWorkMinutes = planned_work_minutes?.toInt() ?: 0,
        plannedBreakMinutes = planned_break_minutes?.toInt() ?: 0,
        plannedLongBreakMinutes = 0,

        totalFocusSeconds = total_focus_seconds?.toInt() ?: 0,
        totalBreakSeconds = total_break_seconds?.toInt() ?: 0,
        totalPausedSeconds = total_paused_seconds?.toInt() ?: 0,

        completedWorkRounds = completed_work_rounds?.toInt() ?: 0,
        completedBreakRounds = completed_break_rounds?.toInt() ?: 0,

        createdAtMillis = created_at.toMillisFromDateTimeText(),
        updatedAtMillis = updated_at.toMillisFromDateTimeText(),

        syncStatus = sync_status.toEnumOrDefault(SyncStatus.LOCAL_ONLY)
    )
}

fun LearningSessionRecord.insertInto(
    queries: SessionHistoryLocalQueries
) {
    queries.insertSessionHistory(
        id = sessionId,
        user_id = userId,
        anonymous_user_id = anonymousUserId,
        session_mode = sessionMode.name,
        status = status.name,
        current_learning_mode = currentLearningMode.name,

        started_at = startedAtMillis.toDateTimeText(),
        ended_at = endedAtMillis.toDateTimeTextOrNull(),
        last_paused_at = lastPausedAtMillis.toDateTimeTextOrNull(),

        planned_work_minutes = plannedWorkMinutes.toLong(),
        planned_break_minutes = plannedBreakMinutes.toLong(),
        completed_work_rounds = completedWorkRounds.toLong(),
        completed_break_rounds = completedBreakRounds.toLong(),

        total_focus_seconds = totalFocusSeconds.toLong(),
        total_break_seconds = totalBreakSeconds.toLong(),
        total_paused_seconds = totalPausedSeconds.toLong(),

        created_at = createdAtMillis.toDateTimeText(),
        updated_at = updatedAtMillis.toDateTimeText(),

        sync_status = syncStatus.name
    )
}

fun SessionHistoryLocalQueries.selectAllLearningSessionRecords(): List<LearningSessionRecord> {
    return selectAllSessionHistory()
        .executeAsList()
        .map { row -> row.toLearningSessionRecord() }
}