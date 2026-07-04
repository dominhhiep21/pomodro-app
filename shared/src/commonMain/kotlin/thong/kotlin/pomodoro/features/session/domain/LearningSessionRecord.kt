package thong.kotlin.pomodoro.features.session.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import thong.kotlin.pomodoro.core.config.AppConfig
import thong.kotlin.pomodoro.core.utils.toEnumOrDefault
import thong.kotlin.pomodoro.core.utils.toMillisFromDateTimeText
import thong.kotlin.pomodoro.core.utils.toMillisFromDateTimeTextOrNull
import thong.kotlin.pomodoro.database.Session_history_local
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro.music.data.MusicRepository
import thong.kotlin.pomodoro.features.settings.data.BackgroundRepository
import kotlin.time.Clock

@Serializable
data class LearningSessionRecord(
    val sessionId: String,
    val userId: String? = null,
    val anonymousUserId: String? = null,
    val sessionMode: LearningStyle = LearningStyle.SOLO,
    val status: LearningSessionStatus = LearningSessionStatus.IDLE,
    val currentLearningMode: CurrentLearningMode = CurrentLearningMode.NOT_YET_STARTED,
    val startedAtMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val endedAtMillis: Long? = null,
    val lastPausedAtMillis: Long? = null,
    val plannedWorkMinutes: Int = AppConfig.DEFAULT_WORK_MINUTES,
    val plannedBreakMinutes: Int = AppConfig.DEFAULT_BREAK_MINUTES,
    val plannedLongBreakMinutes: Int = AppConfig.DEFAULT_LONG_BREAK_MINUTES,
    val lastBackgroundId: String? = BackgroundRepository.DEFAULT_BACKGROUND_ID,
    val lastMusicId: String? = MusicRepository.DEFAULT_TRACK_ID,
    val lastAmbientSounds: Set<String> = emptySet(),
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

        startedAtMillis = started_at_millis ?: started_at.toMillisFromDateTimeText(),
        endedAtMillis = ended_at_millis ?: ended_at.toMillisFromDateTimeTextOrNull(),
        lastPausedAtMillis = last_paused_at_millis ?: last_paused_at.toMillisFromDateTimeTextOrNull(),

        plannedWorkMinutes = planned_work_minutes?.toInt() ?: 0,
        plannedBreakMinutes = planned_break_minutes?.toInt() ?: 0,
        plannedLongBreakMinutes = planned_long_break_minutes?.toInt() ?: 0,

        lastBackgroundId = last_background_id,
        lastMusicId = last_music_id,
        lastAmbientSounds = runCatching {
            Json.decodeFromString<Set<String>>(
                last_ambient_sound_json
                    ?.takeIf { it.isNotBlank() }
                    ?: "[]"
            )
        }.getOrDefault(emptySet()),

        totalFocusSeconds = total_focus_seconds?.toInt() ?: 0,
        totalBreakSeconds = total_break_seconds?.toInt() ?: 0,
        totalPausedSeconds = total_paused_seconds?.toInt() ?: 0,

        completedWorkRounds = completed_work_rounds?.toInt() ?: 0,
        completedBreakRounds = completed_break_rounds?.toInt() ?: 0,

        createdAtMillis = created_at_millis ?: created_at.toMillisFromDateTimeText(),
        updatedAtMillis = updated_at_millis ?: updated_at.toMillisFromDateTimeText(),

        syncStatus = sync_status.toEnumOrDefault(SyncStatus.LOCAL_ONLY)
    )
}