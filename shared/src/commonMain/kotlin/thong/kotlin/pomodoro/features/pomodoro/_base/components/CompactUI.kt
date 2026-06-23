package thong.kotlin.pomodoro.features.pomodoro._base.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.features.pomodoro._base.domain.CompactSection
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TasksUiState
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TimerUiState
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.WorkspaceUiState
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.CompactFloatingTimerComponent

@Composable
fun LandscapeCompactUI(
    workspaceUiState: WorkspaceUiState,
    timerUiState: TimerUiState,
    tasksUiState: TasksUiState,
    onToggleTimer: () -> Unit,
    onToggleCompactMode: () -> Unit,
    onToggleCompactMenu: () -> Unit,
    onSelectCompactSection: (CompactSection) -> Unit,
    onCloseCompactSection: () -> Unit,
    onToggleMusic: () -> Unit,
    onSelectTrack: (String) -> Unit,
    onToggleAmbientSound: (String) -> Unit,
    onSelectBackground: (String) -> Unit,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    onWorkChange: (String) -> Unit,
    onBreakChange: (String) -> Unit,
    onSaveSettings: () -> Unit,
    onResetSettings: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        CompactFloatingTimerComponent(
            timeLeft = timerUiState.timeLeft,
            mode = workspaceUiState.currentMode,
            isActive = timerUiState.isActive,
            onToggle = onToggleTimer,
            width = 200.dp,
            height = 80.dp,
            timeFontSize = 28.sp,
            buttonSize = 48.dp,
            showExitShortcut = true,
            onExitClick = onToggleCompactMode,
            modifier = Modifier.align(Alignment.Center)
        )

        CompactMenuComponent(
            isExpanded = workspaceUiState.isCompactMenuExpanded,
            onToggleExpand = onToggleCompactMenu,
            onSelectSection = onSelectCompactSection,
            onExitCompactMode = onToggleCompactMode,
            isLandscape = true,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        CompactSectionOverlayComponent(
            activeSection = workspaceUiState.activeCompactSection,
            onClose = onCloseCompactSection
        ) { section ->
            CompactSectionUiComponent(
                section = section,
                workspaceUiState = workspaceUiState,
                tasksUiState = tasksUiState,
                onToggleMusic = onToggleMusic,
                onSelectTrack = onSelectTrack,
                onToggleAmbientSound = onToggleAmbientSound,
                onSelectBackground = onSelectBackground,
                onAddTask = onAddTask,
                onDeleteTask = onDeleteTask,
                onToggleTask = onToggleTask,
                onNewTaskTextChange = onNewTaskTextChange,
                onWorkChange = onWorkChange,
                onBreakChange = onBreakChange,
                onSaveSettings = onSaveSettings,
                onResetSettings = onResetSettings
            )
        }
    }
}

@Composable
fun PortraitCompactUI(
    workspaceUiState: WorkspaceUiState,
    timerUiState: TimerUiState,
    tasksUiState: TasksUiState,
    onToggleTimer: () -> Unit,
    onToggleCompactMode: () -> Unit,
    onToggleCompactMenu: () -> Unit,
    onSelectCompactSection: (CompactSection) -> Unit,
    onCloseCompactSection: () -> Unit,
    onToggleMusic: () -> Unit,
    onSelectTrack: (String) -> Unit,
    onToggleAmbientSound: (String) -> Unit,
    onSelectBackground: (String) -> Unit,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    onWorkChange: (String) -> Unit,
    onBreakChange: (String) -> Unit,
    onSaveSettings: () -> Unit,
    onResetSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        CompactFloatingTimerComponent(
            timeLeft = timerUiState.timeLeft,
            mode = workspaceUiState.currentMode,
            isActive = timerUiState.isActive,
            onToggle = onToggleTimer,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        CompactMenuComponent(
            isExpanded = workspaceUiState.isCompactMenuExpanded,
            onToggleExpand = onToggleCompactMenu,
            onSelectSection = onSelectCompactSection,
            onExitCompactMode = onToggleCompactMode,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        CompactSectionOverlayComponent(
            activeSection = workspaceUiState.activeCompactSection,
            onClose = onCloseCompactSection
        ) { section ->
            CompactSectionUiComponent(
                section = section,
                workspaceUiState = workspaceUiState,
                tasksUiState = tasksUiState,
                onToggleMusic = onToggleMusic,
                onSelectTrack = onSelectTrack,
                onToggleAmbientSound = onToggleAmbientSound,
                onSelectBackground = onSelectBackground,
                onAddTask = onAddTask,
                onDeleteTask = onDeleteTask,
                onToggleTask = onToggleTask,
                onNewTaskTextChange = onNewTaskTextChange,
                onWorkChange = onWorkChange,
                onBreakChange = onBreakChange,
                onSaveSettings = onSaveSettings,
                onResetSettings = onResetSettings
            )
        }
    }
}