package thong.kotlin.pomodoro.features.pomodoro._base.domain.model

import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus

data class UserSettingsV2(
    val personalWorkMinutes: Int = 25,
    val personalBreakMinutes: Int = 5,
    val personalLongBreakMinutes: Int = 15,
    val autoStartBreak: Boolean = false,
    val autoStartWork: Boolean = false,
    val personalSelectedBackgroundId: String? = null,
    val personalLastSelectedMusicId: String? = null,
    val hasCompletedOnboarding: Boolean = false,
    val isSessionStopped : Boolean = true,
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