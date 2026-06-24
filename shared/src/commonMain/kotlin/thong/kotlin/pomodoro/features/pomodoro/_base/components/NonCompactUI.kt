package thong.kotlin.pomodoro.features.pomodoro._base.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import thong.kotlin.pomodoro.features.pomodoro.ambient.presentation.components.AmbientSoundSection
import thong.kotlin.pomodoro.features.pomodoro.music.presentation.MusicSection
import thong.kotlin.pomodoro.features.pomodoro.task.components.TaskBottomBar
import thong.kotlin.pomodoro.features.pomodoro.task.components.TaskSideBar
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.TimerSectionComponent
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TasksUiState
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TimerUiState
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.WorkspaceUiState

@Composable
fun LandscapePomodoroUI(
    workspaceUiState: WorkspaceUiState,
    timerUiState: TimerUiState,
    tasksUiState: TasksUiState,
    themeColor: Color,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit,
    onToggleSettings: () -> Unit,
    onToggleCompactMode: () -> Unit,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    onToggleMusic: () -> Unit,
    onSelectTrack: (String) -> Unit,
    onToggleAmbientSound: (String) -> Unit,
    onToggleTasksExpanded: () -> Unit,
    onSelectBackground: (String) -> Unit,
    onExit: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 20.dp, start = 40.dp, end = 92.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            ) {
                ApplicationHeaderComponent(onToggleCompactMode, onToggleSettings, onExit)

                DailyPomoBadge(count = timerUiState.pomodorosToday)

                Box(modifier = Modifier.fillMaxWidth()) {
                    TimerSectionComponent(
                        timerUiState = timerUiState,
                        workspaceUiState = workspaceUiState,
                        themeColor = themeColor,
                        onToggleTimer = onToggleTimer,
                        onResetTimer = onResetTimer,
                        onSkipTimer = onSkipTimer,
                        compact = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            ) {

                MusicSection(
                    availableTracks = workspaceUiState.availableTracks,
                    selectedTrackId = workspaceUiState.selectedTrackId,
                    isMusicPlaying = workspaceUiState.isMusicPlaying,
                    onToggleMusic = onToggleMusic,
                    onSelectTrack = onSelectTrack,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                BackgroundSection(
                    availableBackgrounds = workspaceUiState.availableBackgrounds,
                    selectedBackgroundId = workspaceUiState.selectedBackgroundId,
                    onSelectBackground = onSelectBackground,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                AmbientSoundSection(
                    availableSounds = workspaceUiState.availableAmbientSounds,
                    activeSoundIds = workspaceUiState.activeAmbientSoundIds,
                    onToggleSound = onToggleAmbientSound,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        TaskSideBar(
            tasks = tasksUiState.tasks,
            isExpanded = tasksUiState.isTasksExpanded,
            onToggleExpand = onToggleTasksExpanded,
            newTaskText = tasksUiState.newTaskText,
            onAddTask = onAddTask,
            onDeleteTask = onDeleteTask,
            onToggleTask = onToggleTask,
            onNewTaskTextChange = onNewTaskTextChange,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun LandscapePomodoroGroupUI(
    workspaceUiState: WorkspaceUiState,
    timerUiState: TimerUiState,
    tasksUiState: TasksUiState,
    themeColor: Color,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit,
    onToggleSettings: () -> Unit,
    onToggleCompactMode: () -> Unit,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    onToggleMusic: () -> Unit,
    onSelectTrack: (String) -> Unit,
    onToggleAmbientSound: (String) -> Unit,
    onToggleTasksExpanded: () -> Unit,
    onSelectBackground: (String) -> Unit,
    onExit: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 20.dp, start = 40.dp, end = 92.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    TimerSectionComponent(
                        timerUiState = timerUiState,
                        workspaceUiState = workspaceUiState,
                        themeColor = themeColor,
                        onToggleTimer = onToggleTimer,
                        onResetTimer = onResetTimer,
                        onSkipTimer = onSkipTimer,
                        compact = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            ) {

                MusicSection(
                    availableTracks = workspaceUiState.availableTracks,
                    selectedTrackId = workspaceUiState.selectedTrackId,
                    isMusicPlaying = workspaceUiState.isMusicPlaying,
                    onToggleMusic = onToggleMusic,
                    onSelectTrack = onSelectTrack,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                BackgroundSection(
                    availableBackgrounds = workspaceUiState.availableBackgrounds,
                    selectedBackgroundId = workspaceUiState.selectedBackgroundId,
                    onSelectBackground = onSelectBackground,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                AmbientSoundSection(
                    availableSounds = workspaceUiState.availableAmbientSounds,
                    activeSoundIds = workspaceUiState.activeAmbientSoundIds,
                    onToggleSound = onToggleAmbientSound,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        TaskSideBar(
            tasks = tasksUiState.tasks,
            isExpanded = tasksUiState.isTasksExpanded,
            onToggleExpand = onToggleTasksExpanded,
            newTaskText = tasksUiState.newTaskText,
            onAddTask = onAddTask,
            onDeleteTask = onDeleteTask,
            onToggleTask = onToggleTask,
            onNewTaskTextChange = onNewTaskTextChange,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun PortraitPomodoroUI(
    workspaceUiState: WorkspaceUiState,
    timerUiState: TimerUiState,
    tasksUiState: TasksUiState,
    themeColor: Color,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit,
    onToggleSettings: () -> Unit,
    onToggleCompactMode: () -> Unit,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    onToggleMusic: () -> Unit,
    onSelectTrack: (String) -> Unit,
    onToggleAmbientSound: (String) -> Unit,
    onToggleTasksExpanded: () -> Unit,
    onSelectBackground: (String) -> Unit,
    onExit: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ApplicationHeaderComponent(onToggleCompactMode, onToggleSettings, onExit)

            DailyPomoBadge(count = timerUiState.pomodorosToday)

            Box(modifier = Modifier.fillMaxWidth()) {
                TimerSectionComponent(
                    timerUiState = timerUiState,
                    workspaceUiState = workspaceUiState,
                    themeColor = themeColor,
                    onToggleTimer = onToggleTimer,
                    onResetTimer = onResetTimer,
                    onSkipTimer = onSkipTimer,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            MusicSection(
                availableTracks = workspaceUiState.availableTracks,
                selectedTrackId = workspaceUiState.selectedTrackId,
                isMusicPlaying = workspaceUiState.isMusicPlaying,
                onToggleMusic = onToggleMusic,
                onSelectTrack = onSelectTrack,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            BackgroundSection(
                availableBackgrounds = workspaceUiState.availableBackgrounds,
                selectedBackgroundId = workspaceUiState.selectedBackgroundId,
                onSelectBackground = onSelectBackground,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            AmbientSoundSection(
                availableSounds = workspaceUiState.availableAmbientSounds,
                activeSoundIds = workspaceUiState.activeAmbientSoundIds,
                onToggleSound = onToggleAmbientSound,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(100.dp))
        }

        TaskBottomBar(
            tasks = tasksUiState.tasks,
            isExpanded = tasksUiState.isTasksExpanded,
            onToggleExpand = onToggleTasksExpanded,
            newTaskText = tasksUiState.newTaskText,
            onAddTask = onAddTask,
            onDeleteTask = onDeleteTask,
            onToggleTask = onToggleTask,
            onNewTaskTextChange = onNewTaskTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .align(Alignment.BottomCenter)
        )
    }
}