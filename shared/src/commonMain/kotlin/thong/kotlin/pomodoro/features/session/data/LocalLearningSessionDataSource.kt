package thong.kotlin.pomodoro.features.session.data

import com.russhwolf.settings.Settings
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.LearningSessionState
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus

class LocalLearningSessionDataSource(
    private val settings: Settings
) {

    companion object {
        private const val KEY_SESSION_ID = "learning_session_id"
        private const val KEY_STATUS = "learning_session_status"
        private const val KEY_REMAINING_SECONDS = "learning_session_remaining_seconds"
        private const val KEY_CURRENT_ROUND = "learning_session_current_round"
        private const val KEY_STARTED_AT = "learning_session_started_at"
        private const val KEY_PAUSED_AT = "learning_session_paused_at"
        private const val KEY_ENDED_AT = "learning_session_ended_at"
        private const val KEY_COMPLETED_BY_USER = "learning_session_completed_by_user"

        private const val NULL_LONG = -1L
    }

    fun getCurrentSession(): LearningSessionState {
        val sessionId = settings.getStringOrNull(KEY_SESSION_ID) ?: return LearningSessionState()

        val statusName = settings.getString(
            key = KEY_STATUS,
            defaultValue = LearningSessionStatus.IDLE.name
        )

        val status = runCatching {
            LearningSessionStatus.valueOf(statusName)
        }.getOrDefault(LearningSessionStatus.IDLE)

        val startedAt = settings.getLong(KEY_STARTED_AT, NULL_LONG)
            .takeIf { it != NULL_LONG }

        val pausedAt = settings.getLong(KEY_PAUSED_AT, NULL_LONG)
            .takeIf { it != NULL_LONG }

        val endedAt = settings.getLong(KEY_ENDED_AT, NULL_LONG)
            .takeIf { it != NULL_LONG }

        return LearningSessionState(
            sessionId = sessionId,
            status = status,
            remainingSeconds = settings.getInt(KEY_REMAINING_SECONDS, 25 * 60),
            currentRound = settings.getInt(KEY_CURRENT_ROUND, 1),
            startedAtMillis = startedAt,
            pausedAtMillis = pausedAt,
            endedAtMillis = endedAt,
            completedByUser = settings.getBoolean(KEY_COMPLETED_BY_USER, false)
        )
    }

    fun saveCurrentSession(session: LearningSessionState) {
        if (session.sessionId == null) {
            clearCurrentSession()
            return
        }

        settings.putString(KEY_SESSION_ID, session.sessionId)
        settings.putString(KEY_STATUS, session.status.name)

        settings.putInt(KEY_REMAINING_SECONDS, session.remainingSeconds)
        settings.putInt(KEY_CURRENT_ROUND, session.currentRound)

        settings.putLong(KEY_STARTED_AT, session.startedAtMillis ?: NULL_LONG)
        settings.putLong(KEY_PAUSED_AT, session.pausedAtMillis ?: NULL_LONG)
        settings.putLong(KEY_ENDED_AT, session.endedAtMillis ?: NULL_LONG)

        settings.putBoolean(KEY_COMPLETED_BY_USER, session.completedByUser)
    }

    fun clearCurrentSession() {
        settings.remove(KEY_SESSION_ID)
        settings.remove(KEY_STATUS)
        settings.remove(KEY_REMAINING_SECONDS)
        settings.remove(KEY_CURRENT_ROUND)
        settings.remove(KEY_STARTED_AT)
        settings.remove(KEY_PAUSED_AT)
        settings.remove(KEY_ENDED_AT)
        settings.remove(KEY_COMPLETED_BY_USER)
    }
}