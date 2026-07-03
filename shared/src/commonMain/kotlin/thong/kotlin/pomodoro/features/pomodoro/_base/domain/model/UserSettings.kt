package thong.kotlin.pomodoro.features.pomodoro._base.domain.model

import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle

data class UserSettings(
    val workMinutes: Int = 25,
    val breakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val autoStartBreak: Boolean = false,
    val autoStartWork: Boolean = false,
    val selectedBackgroundId: String? = null,
    val isNotificationEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val isCompactMode: Boolean = false,
    val isMinimalMode: Boolean = false,
    val isBatterySaverEnabled: Boolean = false,
    val maxGroupSize: Int = 4,
    val learningStyle: LearningStyle = LearningStyle.SOLO,
    val hasCompletedOnboarding: Boolean = false
)