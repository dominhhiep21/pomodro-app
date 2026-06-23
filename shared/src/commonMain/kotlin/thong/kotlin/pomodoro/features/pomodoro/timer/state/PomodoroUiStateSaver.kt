package thong.kotlin.pomodoro.features.pomodoro.timer.state

import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.graphics.Color
import thong.kotlin.pomodoro.features.background.model.BackgroundConfig
import thong.kotlin.pomodoro.features.background.model.BackgroundType
import thong.kotlin.pomodoro.features.background.model.PerformanceMode
import thong.kotlin.pomodoro.features.pomodoro._base.domain.EventType
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroConfig
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.Task
import thong.kotlin.pomodoro.features.pomodoro._base.domain.CompactSection

/**
 * Custom saver for PomodoroUiState to persist all data during configuration changes.
 * It flattens nested objects into a list of primitives for efficient storage.
 */
val PomodoroUiStateSaver = listSaver<PomodoroUiState, Any>(
    save = {
        listOf(
            it.currentMode.name,                // 0
            it.timeLeft,                       // 1
            it.isActive,                       // 2
            it.pomodorosToday,                 // 3
            it.event.name,                     // 4
            it.newTaskText,                    // 5
            it.tasks.map { task ->             // 6
                listOf(task.id, task.text, task.isCompleted)
            },
            it.selectedTrackId as Any? ?: "",   // 7 (handle nulls safely)
            it.isMusicPlaying,                 // 8
            it.musicPosition,                  // 9
            it.selectedBackgroundId as Any? ?: "", // 10
            it.isTasksExpanded,                // 11
            it.config.workMinutes,             // 12
            it.config.shortBreakMinutes,       // 13
            it.config.longBreakMinutes,        // 14
            it.backgroundConfig.type.name,     // 15
            it.backgroundConfig.performanceMode.name, // 16
            it.backgroundConfig.primaryColor.value.toLong(), // 17
            it.backgroundConfig.secondaryColor.value.toLong(), // 18
            it.backgroundConfig.speed,         // 19
            it.backgroundConfig.intensity,     // 20
            it.isSettingsVisible,              // 21
            it.editingWorkMinutes,             // 22
            it.editingBreakMinutes,            // 23
            it.settingsError ?: "",            // 24
            it.isCompactMode,                  // 25
            it.isCompactMenuExpanded,          // 26
            it.activeCompactSection?.name ?: "", // 27
            it.isNotificationEnabled,          // 28
            it.activeAmbientSoundIds.toList()   // 29
        )
    },
    restore = {
        @Suppress("UNCHECKED_CAST")
        val tasksRaw = it[6] as List<List<Any>>
        val restoredTasks = tasksRaw.map { rawTask ->
            Task(
                id = rawTask[0] as String,
                text = rawTask[1] as String,
                isCompleted = rawTask[2] as Boolean
            )
        }
        
        @Suppress("UNCHECKED_CAST")
        val restoredAmbientIds = (it[29] as List<String>).toSet()

        PomodoroUiState(
            currentMode = PomodoroMode.valueOf(it[0] as String),
            timeLeft = it[1] as Long,
            isActive = it[2] as Boolean,
            pomodorosToday = it[3] as Int,
            event = EventType.valueOf(it[4] as String),
            newTaskText = it[5] as String,
            tasks = restoredTasks,
            selectedTrackId = (it[7] as String).takeIf { s -> s.isNotEmpty() },
            isMusicPlaying = it[8] as Boolean,
            musicPosition = it[9] as Long,
            selectedBackgroundId = (it[10] as String).takeIf { s -> s.isNotEmpty() },
            isTasksExpanded = it[11] as Boolean,
            config = PomodoroConfig(
                workMinutes = it[12] as Int,
                shortBreakMinutes = it[13] as Int,
                longBreakMinutes = it[14] as Int
            ),
            backgroundConfig = BackgroundConfig(
                type = BackgroundType.valueOf(it[15] as String),
                performanceMode = PerformanceMode.valueOf(it[16] as String),
                primaryColor = Color((it[17] as Long).toULong()),
                secondaryColor = Color((it[18] as Long).toULong()),
                speed = it[19] as Float,
                intensity = it[20] as Float
            ),
            isSettingsVisible = it[21] as Boolean,
            editingWorkMinutes = it[22] as String,
            editingBreakMinutes = it[23] as String,
            settingsError = (it[24] as String).takeIf { s -> s.isNotEmpty() },
            isCompactMode = it[25] as Boolean,
            isCompactMenuExpanded = it[26] as Boolean,
            activeCompactSection = (it[27] as String).takeIf { s -> s.isNotEmpty() }?.let { name ->
                CompactSection.valueOf(name)
            },
            isNotificationEnabled = it[28] as Boolean,
            activeAmbientSoundIds = restoredAmbientIds
        )
    }
)
