package thong.kotlin.pomodoro.features.pomodoro.timer.state

import androidx.compose.runtime.Immutable
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.EventType
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.PomodoroConfig
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.Task
import thong.kotlin.pomodoro.features.pomodoro.music.domain.MusicTrack
import thong.kotlin.pomodoro.features.pomodoro.ambient.domain.AmbientSound
import thong.kotlin.pomodoro.features.pomodoro.domain.model.LearningStyle
import thong.kotlin.pomodoro.features.background.model.BackgroundConfig
import thong.kotlin.pomodoro.features.background.model.BackgroundType
import thong.kotlin.pomodoro.features.background.model.PerformanceMode

import thong.kotlin.pomodoro.features.settings.domain.AppBackground

@Immutable
data class PomodoroUiState(
    val currentMode: PomodoroMode = PomodoroMode.WORK,
    val config: PomodoroConfig = PomodoroConfig(),
    val timeLeft: Long = currentMode.totalSeconds(config),
    val isActive: Boolean = false,
    val pomodorosToday: Int = 0,
    val event: EventType = EventType.NOTHING,
    val tasks: List<Task> = emptyList(),
    val newTaskText: String = "",
    val availableTracks: List<MusicTrack> = emptyList(),
    val selectedTrackId: String? = null,
    val isMusicPlaying: Boolean = false,
    val musicPosition: Long = 0L,
    val availableAmbientSounds: List<AmbientSound> = emptyList(),
    val activeAmbientSoundIds: Set<String> = emptySet(),
    val availableBackgrounds: List<AppBackground> = emptyList(),
    val selectedBackgroundId: String? = null,
    val isTasksExpanded: Boolean = false,
    val backgroundConfig: BackgroundConfig = BackgroundConfig(
        type = BackgroundType.DYNAMIC_AURA,
        performanceMode = PerformanceMode.BALANCED
    ),
    val isSettingsVisible: Boolean = false,
    val editingWorkMinutes: String = "",
    val editingBreakMinutes: String = "",
    val settingsError: String? = null,
    val isCompactMode: Boolean = false,
    val isCompactMenuExpanded: Boolean = false,
    val activeCompactSection: CompactSection? = null,
    val learningStyle: LearningStyle = LearningStyle.SOLO,
    val isNotificationEnabled: Boolean = true,
    val pendingNotification: String? = null
) {
    val incompleteTaskCount: Int
        get() = tasks.count { !it.isCompleted }

    val isJustEndedBreak: Boolean
        get() = currentMode == PomodoroMode.WORK &&
                timeLeft == currentMode.totalSeconds(config) &&
                !isActive && event == EventType.BREAK_END
}

enum class CompactSection {
    TASKS, MUSIC, BACKGROUND, AMBIENT, SETTINGS
}

fun PomodoroMode.totalSeconds(config: PomodoroConfig): Long {
    return when (this) {
        PomodoroMode.WORK -> config.workSeconds.toLong()
        PomodoroMode.SHORT_BREAK -> config.shortBreakSeconds.toLong()
        PomodoroMode.LONG_BREAK -> config.longBreakSeconds.toLong()
    }
}