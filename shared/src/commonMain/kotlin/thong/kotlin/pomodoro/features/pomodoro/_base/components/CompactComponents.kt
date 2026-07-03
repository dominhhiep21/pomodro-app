package thong.kotlin.pomodoro.features.pomodoro._base.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro._base.domain.CompactSection
import thong.kotlin.pomodoro.features.pomodoro.ambient.presentation.components.AmbientSoundSection
import thong.kotlin.pomodoro.features.pomodoro.music.presentation.CompactMusicSectionComponent
import thong.kotlin.pomodoro.features.pomodoro.task.components.CompactTaskSectionComponent
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TotallyPomodoroUiState
import thong.kotlin.pomodoro.features.settings.presentation.components.SettingsUiComponent

@Composable
fun CompactMenuComponent(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectSection: (CompactSection) -> Unit,
    onExitCompactMode: () -> Unit,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        // Expandable Menu Panel
        AnimatedVisibility(
            visible = isExpanded,
            enter = if (isLandscape) fadeIn() + expandHorizontally(expandFrom = Alignment.End)
            else fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit = if (isLandscape) fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
            else fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            GlassBox(
                modifier = if (isLandscape) {
                    Modifier.padding(end = 72.dp).height(56.dp)
                } else {
                    Modifier.padding(bottom = 72.dp).width(56.dp)
                },
                shape = RoundedCornerShape(28.dp),
                backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.7f)
            ) {
                if (isLandscape) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CompactMenuItemComponent(Icons.AutoMirrored.Filled.List, "Tasks") { onSelectSection(CompactSection.TASKS) }
                        CompactMenuItemComponent(Icons.Default.MusicNote, "Music") { onSelectSection(CompactSection.MUSIC) }
                        CompactMenuItemComponent(Icons.Default.GraphicEq, "Ambient") { onSelectSection(CompactSection.AMBIENT) }
                        CompactMenuItemComponent(Icons.Default.Image, "Backgrounds") { onSelectSection(CompactSection.BACKGROUND) }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .clickable { onExitCompactMode() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ZoomOutMap, "Exit", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CompactMenuItemComponent(Icons.AutoMirrored.Filled.List, "Tasks") { onSelectSection(CompactSection.TASKS) }
                        CompactMenuItemComponent(Icons.Default.MusicNote, "Music") { onSelectSection(CompactSection.MUSIC) }
                        CompactMenuItemComponent(Icons.Default.GraphicEq, "Ambient") { onSelectSection(CompactSection.AMBIENT) }
                        CompactMenuItemComponent(Icons.Default.Image, "Backgrounds") { onSelectSection(CompactSection.BACKGROUND) }
                        CompactMenuItemComponent(Icons.Default.Settings, "Settings") { onSelectSection(CompactSection.SETTINGS) }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .clickable { onExitCompactMode() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ZoomOutMap, "Exit", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        // Toggle FAB
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(AuraColors.WorkMode, CircleShape)
                .clickable { onToggleExpand() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun CompactMenuItemComponent(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun CompactSectionOverlayComponent(
    activeSection: CompactSection?,
    onClose: () -> Unit,
    content: @Composable (CompactSection) -> Unit
) {
    AnimatedVisibility(
        visible = activeSection != null,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(max = 500.dp)
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.85f)
            ) {
                GlassBox(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = false) { },
                    shape = RoundedCornerShape(28.dp),
                    backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.95f)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                        activeSection?.let { content(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactSectionUiComponent(
    totallyPomodoroUiState: TotallyPomodoroUiState,
    section: CompactSection,
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
    when (section) {
        CompactSection.TASKS -> {
            CompactTaskSectionComponent(
                sessionTasks = totallyPomodoroUiState.tasksUiState.sessionTasks,
                newTaskText = totallyPomodoroUiState.tasksUiState.newTaskText,
                onAddTask = onAddTask,
                onDeleteTask = onDeleteTask,
                onToggleTask = onToggleTask,
                onNewTaskTextChange = onNewTaskTextChange,
                useLazyColumn = true,
                modifier = Modifier.fillMaxSize()
            )
        }

        CompactSection.MUSIC -> {
            CompactMusicSectionComponent(
                availableTracks = totallyPomodoroUiState.workspaceUiState.availableTracks,
                selectedTrackId = totallyPomodoroUiState.workspaceUiState.selectedTrackId,
                isMusicPlaying = totallyPomodoroUiState.workspaceUiState.isMusicPlaying,
                onToggleMusic = onToggleMusic,
                onSelectTrack = onSelectTrack,
                modifier = Modifier.fillMaxSize()
            )
        }

        CompactSection.BACKGROUND -> {
            BackgroundSection(
                availableBackgrounds = totallyPomodoroUiState.workspaceUiState.availableBackgrounds,
                selectedBackgroundId = totallyPomodoroUiState.workspaceUiState.selectedBackgroundId,
                onSelectBackground = onSelectBackground,
                modifier = Modifier.fillMaxSize()
            )
        }

        CompactSection.AMBIENT -> {
            AmbientSoundSection(
                availableSounds = totallyPomodoroUiState.workspaceUiState.availableAmbientSounds,
                activeSoundIds = totallyPomodoroUiState.workspaceUiState.activeAmbientSoundIds,
                onToggleSound = onToggleAmbientSound,
                modifier = Modifier.fillMaxSize()
            )
        }

        CompactSection.SETTINGS -> {
            SettingsUiComponent(
                totallyPomodoroUiState = totallyPomodoroUiState,
                onWorkChange = onWorkChange,
                onBreakChange = onBreakChange,
                onSave = onSaveSettings,
                onReset = onResetSettings
            )
        }
    }
}