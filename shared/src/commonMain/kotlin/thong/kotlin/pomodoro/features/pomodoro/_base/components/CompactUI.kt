package thong.kotlin.pomodoro.features.pomodoro._base.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.features.learning.mode.components.ExpandableChatPanel
import thong.kotlin.pomodoro.features.learning.mode.components.ExpandableMembersPanelBubble
import thong.kotlin.pomodoro.features.learning.mode.components.RoomIdBadge
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro._base.domain.CompactSection
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.CompactFloatingTimerComponent
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TotallyPomodoroUiState

@Composable
fun LandscapeCompactUI(
    totallyPomodoroUiState: TotallyPomodoroUiState,
    learningStyle: LearningStyle = LearningStyle.SOLO,
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
    onToggleSettings: () -> Unit = {},
    onSaveSettings: () -> Unit,
    onResetSettings: () -> Unit,
    onExit: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp)) {

        if (learningStyle == LearningStyle.GROUP) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start)
            ) {
                RoomIdBadge("1234")
                DailyPomoBadge(count = totallyPomodoroUiState.timerUiState.pomodorosToday)
            }
        } else {
            DailyPomoBadge(count = totallyPomodoroUiState.timerUiState.pomodorosToday)
        }

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onExit
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Exit to Style Selection",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }

        CompactFloatingTimerComponent(
            timeLeft = totallyPomodoroUiState.timerUiState.timeLeft,
            mode = totallyPomodoroUiState.currentMode,
            isActive = totallyPomodoroUiState.timerUiState.isActive,
            onToggle = onToggleTimer,
            width = 200.dp,
            height = 80.dp,
            timeFontSize = 28.sp,
            buttonSize = 48.dp,
            showExitShortcut = true,
            onExitClick = onToggleCompactMode,
            modifier = Modifier.align(Alignment.Center),
            onToggleSettings = onToggleSettings
        )

        CompactMenuComponent(
            isExpanded = totallyPomodoroUiState.workspaceUiState.isCompactMenuExpanded,
            onToggleExpand = onToggleCompactMenu,
            onSelectSection = onSelectCompactSection,
            onExitCompactMode = onToggleCompactMode,
            isLandscape = true,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        if (learningStyle == LearningStyle.GROUP) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                ExpandableMembersPanelBubble()
                ExpandableChatPanel()
            }
        }

        CompactSectionOverlayComponent(
            activeSection = totallyPomodoroUiState.workspaceUiState.activeCompactSection,
            onClose = onCloseCompactSection
        ) { section ->
            CompactSectionUiComponent(
                totallyPomodoroUiState = totallyPomodoroUiState,
                section = section,
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
    totallyPomodoroUiState: TotallyPomodoroUiState,
    learningStyle: LearningStyle = LearningStyle.SOLO,
    onToggleSettings: () -> Unit = {},
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
    onResetSettings: () -> Unit,
    onExit: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        CompactFloatingTimerComponent(
            showExitShortcut = true,
            timeLeft = totallyPomodoroUiState.timerUiState.timeLeft,
            mode = totallyPomodoroUiState.currentMode,
            isActive = totallyPomodoroUiState.timerUiState.isActive,
            onToggle = onToggleTimer,
            onToggleSettings = onToggleSettings,
            onExitClick = onToggleCompactMode,
            modifier = Modifier.align(Alignment.Center)
        )

        CompactMenuComponent(
            isExpanded = totallyPomodoroUiState.workspaceUiState.isCompactMenuExpanded,
            onToggleExpand = onToggleCompactMenu,
            onSelectSection = onSelectCompactSection,
            onExitCompactMode = onToggleCompactMode,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        CompactSectionOverlayComponent(
            activeSection = totallyPomodoroUiState.workspaceUiState.activeCompactSection,
            onClose = onCloseCompactSection
        ) { section ->
            CompactSectionUiComponent(
                totallyPomodoroUiState = totallyPomodoroUiState,
                section = section,
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

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onExit
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Exit to Style Selection",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }

        if (learningStyle == LearningStyle.GROUP) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ExpandableMembersPanelBubble()
                ExpandableChatPanel()
            }
        }

        if (learningStyle == LearningStyle.GROUP) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start)
            ) {
                RoomIdBadge("1234")
                DailyPomoBadge(count = totallyPomodoroUiState.timerUiState.pomodorosToday)
            }
        } else {
            DailyPomoBadge(count = totallyPomodoroUiState.timerUiState.pomodorosToday)
        }
    }
}