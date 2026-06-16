package thong.kotlin.pomodoro.features.pomodoro.domain.model

enum class LearningStyle {
    SOLO, GROUP
}

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
    val learningStyle: LearningStyle = LearningStyle.SOLO,
    val hasCompletedOnboarding: Boolean = false
)
