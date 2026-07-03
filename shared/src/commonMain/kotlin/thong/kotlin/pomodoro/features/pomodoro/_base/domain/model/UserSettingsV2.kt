package thong.kotlin.pomodoro.features.pomodoro._base.domain.model

import thong.kotlin.pomodoro.core.config.AppConfig
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus

data class UserSettingsV2(
    val currentSessionId : String? = null,
    val personalWorkMinutes: Int = AppConfig.DEFAULT_WORK_MINUTES,
    val personalBreakMinutes: Int = AppConfig.DEFAULT_BREAK_MINUTES,
    val personalLongBreakMinutes: Int = AppConfig.DEFAULT_LONG_BREAK_MINUTES,
    val autoStartBreak: Boolean = AppConfig.DEFAULT_AUTO_START_BREAK,
    val autoStartWork: Boolean = AppConfig.DEFAULT_AUTO_START_WORK,
    val personalSelectedBackgroundId: String? = null,
    val personalLastSelectedMusicId: String? = null,
    val hasCompletedOnboarding: Boolean = false,
    val isNotificationEnabled: Boolean = false
)

data class PomodoroUiState(
    val isCompact: Boolean,
    val isLandscape: Boolean,
    val style: LearningStyle
)

data class LearningSessionState(
    val sessionId: String? = null,
    val status: LearningSessionStatus = LearningSessionStatus.IDLE,
    val remainingSeconds: Int = 25 * 60,
    val currentRound: Int = 1,
    val startedAtMillis: Long? = null,
    val pausedAtMillis: Long? = null,
    val endedAtMillis: Long? = null,
    val completedByUser: Boolean = false
)