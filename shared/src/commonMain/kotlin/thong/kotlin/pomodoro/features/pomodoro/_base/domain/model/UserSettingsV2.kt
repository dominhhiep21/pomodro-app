package thong.kotlin.pomodoro.features.pomodoro._base.domain.model

import thong.kotlin.pomodoro.core.config.AppConfig
import thong.kotlin.pomodoro.features.background.data.BackgroundRepository
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro.music.data.MusicRepository
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus

data class UserSettingsV2(
    val currentSessionId : String? = null,
    val personalWorkMinutes: Int = AppConfig.DEFAULT_WORK_MINUTES,
    val personalBreakMinutes: Int = AppConfig.DEFAULT_BREAK_MINUTES,
    val personalLongBreakMinutes: Int = AppConfig.DEFAULT_LONG_BREAK_MINUTES,
    val autoStartBreak: Boolean = AppConfig.DEFAULT_AUTO_START_BREAK,
    val autoStartWork: Boolean = AppConfig.DEFAULT_AUTO_START_WORK,
    val personalSelectedBackgroundId: String? = BackgroundRepository.DEFAULT_BACKGROUND_ID,
    val personalLastSelectedMusicId: String? = MusicRepository.DEFAULT_TRACK_ID,
    val hasCompletedOnboarding: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val dailyTargetMinutes: Int = 120,
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val firstDayOfWeek: Int = 1, // 1 for Monday
    val language: String = "vi",
    val isDarkMode: Boolean = true
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