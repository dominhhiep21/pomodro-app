package thong.kotlin.pomodoro.features.pomodoro.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.startup_bg
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.AuraInputField
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.notification.NotificationManager
import thong.kotlin.pomodoro.features.pomodoro.ambient.presentation.components.AmbientSoundSection
import thong.kotlin.pomodoro.features.pomodoro.music.presentation.MusicSection
import thong.kotlin.pomodoro.features.pomodoro.task.presentation.components.TaskBottomBar
import thong.kotlin.pomodoro.features.pomodoro.task.presentation.components.TaskSideBar
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.BackgroundSection
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.CompactFloatingTimer
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.CompactMenu
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.CompactSectionOverlay
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.PomodoroSettingsModal
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.TimerSection
import thong.kotlin.pomodoro.features.pomodoro.timer.state.CompactSection
import thong.kotlin.pomodoro.features.pomodoro.timer.state.PomodoroUiState
import thong.kotlin.pomodoro.features.pomodoro.timer.viewmodel.PomodoroViewModel

class PomodoroScreen(notificationManager: NotificationManager?) : Screen {

    @Composable
    override fun Content() {
    }
}

@Composable
private fun PomodoroScreenResponsive(
    viewModel: PomodoroViewModel,
    onExit: () -> Unit = {},
    notificationManager: NotificationManager? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // --- Side Effect: Show Notifications ---
    LaunchedEffect(uiState.pendingNotification) {
        val message = uiState.pendingNotification
        if (message != null && uiState.isNotificationEnabled) {
            // Show system notification
            notificationManager?.showNotification(
                title = "Aura Pomo",
                message = message
            )
            // Also show in-app snackbar as a fallback/visual confirmation
            snackbarHostState.showSnackbar(message)

            viewModel.clearPendingNotification()
        }
    }

    val animatedThemeColor by animateColorAsState(
        targetValue = when (uiState.currentMode) {
            PomodoroMode.WORK -> AuraColors.WorkMode
            PomodoroMode.SHORT_BREAK -> AuraColors.ShortBreakMode
            PomodoroMode.LONG_BREAK -> AuraColors.LongBreakMode
        },
        animationSpec = tween(durationMillis = 500),
        label = "ThemeColorTransition",
    )

    val currentBackground = uiState.availableBackgrounds.find { it.id == uiState.selectedBackgroundId }

    // --- HD VISUALIZED: Breathing Effect ---
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundBreathing")
    
    // Subtly animate blur and overlay to make the background feel "alive"
    val breathingBlur by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathingBlur"
    )
    
    val breathingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathingAlpha"
    )

    AuraBackground(
        imageRes = currentBackground?.resource ?: Res.drawable.startup_bg,
        landscapeImageRes = currentBackground?.landscapeResource,
        blurRadius = if (uiState.isCompactMode) 0f else breathingBlur,
        overlayAlpha = if (uiState.isCompactMode) 0.15f else breathingAlpha
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val isLandscape = maxWidth > maxHeight

                if (uiState.isCompactMode) {
                    if (isLandscape) {
                        LandscapeCompactContent(
                            uiState = uiState,
                            onToggleTimer = viewModel::toggleTimer,
                            onToggleCompactMode = viewModel::toggleCompactMode,
                            onToggleCompactMenu = viewModel::toggleCompactMenu,
                            onSelectCompactSection = viewModel::setActiveCompactSection,
                            onCloseCompactSection = { viewModel.setActiveCompactSection(null) },
                            // Section-specific actions
                            onToggleMusic = viewModel::toggleMusic,
                            onSelectTrack = viewModel::selectTrack,
                            onToggleAmbientSound = viewModel::toggleAmbientSound,
                            onSelectBackground = viewModel::selectBackground,
                            onAddTask = viewModel::addTask,
                            onDeleteTask = viewModel::deleteTask,
                            onToggleTask = viewModel::toggleTask,
                            onNewTaskTextChange = viewModel::onNewTaskTextChange,
                            onWorkChange = viewModel::onWorkMinutesChange,
                            onBreakChange = viewModel::onBreakMinutesChange,
                            onSaveSettings = viewModel::saveSettings,
                            onResetSettings = viewModel::resetSettingsToDefault
                        )
                    } else {
                        PortraitCompactContent(
                            uiState = uiState,
                            onToggleTimer = viewModel::toggleTimer,
                            onToggleCompactMode = viewModel::toggleCompactMode,
                            onToggleCompactMenu = viewModel::toggleCompactMenu,
                            onSelectCompactSection = viewModel::setActiveCompactSection,
                            onCloseCompactSection = { viewModel.setActiveCompactSection(null) },
                            // Section-specific actions
                            onToggleMusic = viewModel::toggleMusic,
                            onSelectTrack = viewModel::selectTrack,
                            onToggleAmbientSound = viewModel::toggleAmbientSound,
                            onSelectBackground = viewModel::selectBackground,
                            onAddTask = viewModel::addTask,
                            onDeleteTask = viewModel::deleteTask,
                            onToggleTask = viewModel::toggleTask,
                            onNewTaskTextChange = viewModel::onNewTaskTextChange,
                            onWorkChange = viewModel::onWorkMinutesChange,
                            onBreakChange = viewModel::onBreakMinutesChange,
                            onSaveSettings = viewModel::saveSettings,
                            onResetSettings = viewModel::resetSettingsToDefault
                        )
                    }
                } else {
                    if (isLandscape) {
                        LandscapePomodoroContent(
                            uiState = uiState,
                            themeColor = animatedThemeColor,
                            onToggleTimer = viewModel::toggleTimer,
                            onResetTimer = viewModel::resetTimer,
                            onSkipTimer = viewModel::skipTimer,
                            onToggleSettings = viewModel::toggleSettings,
                            onExit = onExit,
                            onToggleCompactMode = viewModel::toggleCompactMode,
                            onAddTask = viewModel::addTask,
                            onDeleteTask = viewModel::deleteTask,
                            onToggleTask = viewModel::toggleTask,
                            onNewTaskTextChange = viewModel::onNewTaskTextChange,
                            onToggleMusic = viewModel::toggleMusic,
                            onSelectTrack = viewModel::selectTrack,
                            onToggleAmbientSound = viewModel::toggleAmbientSound,
                            onToggleTasksExpanded = viewModel::toggleTasksExpanded,
                            onSelectBackground = viewModel::selectBackground
                        )
                    } else {
                        PortraitPomodoroContent(
                            uiState = uiState,
                            themeColor = animatedThemeColor,
                            onToggleTimer = viewModel::toggleTimer,
                            onResetTimer = viewModel::resetTimer,
                            onSkipTimer = viewModel::skipTimer,
                            onToggleSettings = viewModel::toggleSettings,
                            onExit = onExit,
                            onToggleCompactMode = viewModel::toggleCompactMode,
                            onAddTask = viewModel::addTask,
                            onDeleteTask = viewModel::deleteTask,
                            onToggleTask = viewModel::toggleTask,
                            onNewTaskTextChange = viewModel::onNewTaskTextChange,
                            onToggleMusic = viewModel::toggleMusic,
                            onSelectTrack = viewModel::selectTrack,
                            onToggleAmbientSound = viewModel::toggleAmbientSound,
                            onToggleTasksExpanded = viewModel::toggleTasksExpanded,
                            onSelectBackground = viewModel::selectBackground
                        )
                    }
                }
            }

            // In-app Snackbar Host
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            )

            // Settings Modal
            if (uiState.isSettingsVisible) {
                PomodoroSettingsModal(
                    uiState = uiState,
                    onWorkChange = viewModel::onWorkMinutesChange,
                    onBreakChange = viewModel::onBreakMinutesChange,
                    onSave = viewModel::saveSettings,
                    onCancel = viewModel::toggleSettings,
                    onReset = viewModel::resetSettingsToDefault,
                    onHardReset = viewModel::hardResetData
                )
            }
        }
    }
}

@Composable
private fun PortraitCompactContent(
    uiState: PomodoroUiState,
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
        CompactFloatingTimer(
            timeLeft = uiState.timeLeft,
            mode = uiState.currentMode,
            isActive = uiState.isActive,
            onToggle = onToggleTimer,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        CompactMenu(
            isExpanded = uiState.isCompactMenuExpanded,
            onToggleExpand = onToggleCompactMenu,
            onSelectSection = onSelectCompactSection,
            onExitCompactMode = onToggleCompactMode,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        CompactSectionOverlay(
            activeSection = uiState.activeCompactSection,
            onClose = onCloseCompactSection
        ) { section ->
            CompactSectionContent(
                section = section,
                uiState = uiState,
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
private fun LandscapeCompactContent(
    uiState: PomodoroUiState,
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
            .padding(32.dp)
    ) {
        CompactFloatingTimer(
            timeLeft = uiState.timeLeft,
            mode = uiState.currentMode,
            isActive = uiState.isActive,
            onToggle = onToggleTimer,
            width = 200.dp,
            height = 80.dp,
            timeFontSize = 28.sp,
            buttonSize = 48.dp,
            showExitShortcut = true,
            onExitClick = onToggleCompactMode,
            modifier = Modifier.align(Alignment.Center)
        )

        CompactMenu(
            isExpanded = uiState.isCompactMenuExpanded,
            onToggleExpand = onToggleCompactMenu,
            onSelectSection = onSelectCompactSection,
            onExitCompactMode = onToggleCompactMode,
            isLandscape = true,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        CompactSectionOverlay(
            activeSection = uiState.activeCompactSection,
            onClose = onCloseCompactSection
        ) { section ->
            CompactSectionContent(
                section = section,
                uiState = uiState,
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
private fun CompactSectionContent(
    section: CompactSection,
    uiState: PomodoroUiState,
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
            thong.kotlin.pomodoro.features.pomodoro.task.presentation.components.TaskSection(
                tasks = uiState.tasks,
                newTaskText = uiState.newTaskText,
                onAddTask = onAddTask,
                onDeleteTask = onDeleteTask,
                onToggleTask = onToggleTask,
                onNewTaskTextChange = onNewTaskTextChange,
                useLazyColumn = true,
                modifier = Modifier.fillMaxSize()
            )
        }
        CompactSection.MUSIC -> {
            MusicSection(
                availableTracks = uiState.availableTracks,
                selectedTrackId = uiState.selectedTrackId,
                isMusicPlaying = uiState.isMusicPlaying,
                onToggleMusic = onToggleMusic,
                onSelectTrack = onSelectTrack,
                modifier = Modifier.fillMaxSize()
            )
        }
        CompactSection.BACKGROUND -> {
            BackgroundSection(
                availableBackgrounds = uiState.availableBackgrounds,
                selectedBackgroundId = uiState.selectedBackgroundId,
                onSelectBackground = onSelectBackground,
                modifier = Modifier.fillMaxSize()
            )
        }
        CompactSection.AMBIENT -> {
            AmbientSoundSection(
                availableSounds = uiState.availableAmbientSounds,
                activeSoundIds = uiState.activeAmbientSoundIds,
                onToggleSound = onToggleAmbientSound,
                modifier = Modifier.fillMaxSize()
            )
        }
        CompactSection.SETTINGS -> {
            thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.SettingsContent(
                uiState = uiState,
                onWorkChange = onWorkChange,
                onBreakChange = onBreakChange,
                onSave = onSaveSettings,
                onReset = onResetSettings
            )
        }
    }
}

@Composable
private fun PortraitPomodoroContent(
    uiState: PomodoroUiState,
    themeColor: Color,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit,
    onToggleSettings: () -> Unit,
    onExit: () -> Unit,
    onToggleCompactMode: () -> Unit,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    onToggleMusic: () -> Unit,
    onSelectTrack: (String) -> Unit,
    onToggleAmbientSound: (String) -> Unit,
    onToggleTasksExpanded: () -> Unit,
    onSelectBackground: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                TimerSection(
                    uiState = uiState,
                    themeColor = themeColor,
                    onToggleTimer = onToggleTimer,
                    onResetTimer = onResetTimer,
                    onSkipTimer = onSkipTimer,
                    onToggleSettings = onToggleSettings,
                    onExit = onExit,
                    onToggleCompactMode = onToggleCompactMode,
                    modifier = Modifier.fillMaxWidth()
                )

            }


            Spacer(modifier = Modifier.height(24.dp))

            MusicSection(
                availableTracks = uiState.availableTracks,
                selectedTrackId = uiState.selectedTrackId,
                isMusicPlaying = uiState.isMusicPlaying,
                onToggleMusic = onToggleMusic,
                onSelectTrack = onSelectTrack,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            BackgroundSection(
                availableBackgrounds = uiState.availableBackgrounds,
                selectedBackgroundId = uiState.selectedBackgroundId,
                onSelectBackground = onSelectBackground,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            AmbientSoundSection(
                availableSounds = uiState.availableAmbientSounds,
                activeSoundIds = uiState.activeAmbientSoundIds,
                onToggleSound = onToggleAmbientSound,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom bar
        }

        TaskBottomBar(
            tasks = uiState.tasks,
            isExpanded = uiState.isTasksExpanded,
            onToggleExpand = onToggleTasksExpanded,
            newTaskText = uiState.newTaskText,
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

@Composable
private fun LandscapePomodoroContent(
    uiState: PomodoroUiState,
    themeColor: Color,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipTimer: () -> Unit,
    onToggleSettings: () -> Unit,
    onExit: () -> Unit,
    onToggleCompactMode: () -> Unit,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    onToggleMusic: () -> Unit,
    onSelectTrack: (String) -> Unit,
    onToggleAmbientSound: (String) -> Unit,
    onToggleTasksExpanded: () -> Unit,
    onSelectBackground: (String) -> Unit
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
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {

                Box(modifier = Modifier.fillMaxWidth()) {
                    TimerSection(
                        uiState = uiState,
                        themeColor = themeColor,
                        onToggleTimer = onToggleTimer,
                        onResetTimer = onResetTimer,
                        onSkipTimer = onSkipTimer,
                        onToggleSettings = onToggleSettings,
                        onExit = onExit,
                        onToggleCompactMode = onToggleCompactMode,
                        compact = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                }

            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {

                MusicSection(
                    availableTracks = uiState.availableTracks,
                    selectedTrackId = uiState.selectedTrackId,
                    isMusicPlaying = uiState.isMusicPlaying,
                    onToggleMusic = onToggleMusic,
                    onSelectTrack = onSelectTrack,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                BackgroundSection(
                    availableBackgrounds = uiState.availableBackgrounds,
                    selectedBackgroundId = uiState.selectedBackgroundId,
                    onSelectBackground = onSelectBackground,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                AmbientSoundSection(
                    availableSounds = uiState.availableAmbientSounds,
                    activeSoundIds = uiState.activeAmbientSoundIds,
                    onToggleSound = onToggleAmbientSound,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        TaskSideBar(
            tasks = uiState.tasks,
            isExpanded = uiState.isTasksExpanded,
            onToggleExpand = onToggleTasksExpanded,
            newTaskText = uiState.newTaskText,
            onAddTask = onAddTask,
            onDeleteTask = onDeleteTask,
            onToggleTask = onToggleTask,
            onNewTaskTextChange = onNewTaskTextChange,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

// --- Group Learning Components ---

@Composable
private fun RoomIdBadge(roomId: String, modifier: Modifier = Modifier) {
    GlassBox(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PHÒNG: ",
                color = AuraColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "#$roomId",
                color = AuraColors.WorkMode,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun ExpandableMembersPanel(modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }
    val participants = remember {
        listOf("Bạn", "Minh", "Lan", "Phong", "Trang", "Hoàng", "Nam", "An")
    }

    Column(
        modifier = modifier
            .animateContentSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .clickable { isExpanded = !isExpanded }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Thành viên",
                    color = AuraColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                GlassBox(
                    shape = CircleShape,
                    backgroundColor = AuraColors.WorkMode.copy(alpha = 0.2f),
                    modifier = Modifier.size(22.dp)
                ) {
                    Text(
                        text = participants.size.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = AuraColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(participants) { name ->
                    MemberAvatar(name = name)
                }
            }
        } else {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = participants.joinToString(", "),
                color = AuraColors.TextSecondary,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MemberAvatar(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1),
                color = AuraColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            color = AuraColors.TextSecondary,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun ExpandableChatPanel(modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("Minh", "Chào cả nhà!", isMe = false),
            ChatMessage("Lan", "Cố gắng học thôi nào!", isMe = false),
            ChatMessage("Hoàng", "Mọi người tập trung ghê quá :D", isMe = false)
        )
    }

    Column(
        modifier = modifier
            .animateContentSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trò chuyện",
                color = AuraColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = AuraColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg = msg)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AuraInputField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = "Nhắn tin...",
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            messages.add(ChatMessage("Bạn", messageText, true))
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AuraColors.WorkMode.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(6.dp))
            messages.lastOrNull()?.let { lastMsg ->
                Text(
                    text = "${lastMsg.sender}: ${lastMsg.text}",
                    color = AuraColors.TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start
    ) {
        Text(
            text = msg.sender,
            color = AuraColors.TextSecondary,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        GlassBox(
            shape = RoundedCornerShape(12.dp),
            backgroundColor = if (msg.isMe) AuraColors.WorkMode.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Text(
                text = msg.text,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

private data class ChatMessage(
    val sender: String,
    val text: String,
    val isMe: Boolean
)
