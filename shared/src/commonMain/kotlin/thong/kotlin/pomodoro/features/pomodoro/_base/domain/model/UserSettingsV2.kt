package thong.kotlin.pomodoro.features.pomodoro._base.domain.model

import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle

data class UserSettingsV2(
    val personalWorkMinutes: Int = 25,
    val personalBreakMinutes: Int = 5,
    val personalLongBreakMinutes: Int = 15,
    val autoStartBreak: Boolean = false,
    val autoStartWork: Boolean = false,
    val personalSelectedBackgroundId: String? = null,
    val personalLastSelectedMusicId: String? = null,
    val hasCompletedOnboarding: Boolean = false,
    val isNotificationEnabled: Boolean = false
)

data class PomodoroUiStateV2(
    val isCompact: Boolean,
    val isLandscape: Boolean,
    val style: LearningStyle
)