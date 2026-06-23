package thong.kotlin.pomodoro.features.pomodoro._base

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.startup_bg
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TasksViewModel
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TimerViewModel
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.WorkspaceViewModel
import thong.kotlin.pomodoro.features.pomodoro._base.components.LandscapeCompactUI
import thong.kotlin.pomodoro.features.pomodoro._base.components.LandscapePomodoroUI
import thong.kotlin.pomodoro.features.pomodoro._base.components.PortraitCompactUI
import thong.kotlin.pomodoro.features.pomodoro._base.components.PortraitPomodoroUI
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.PomodoroSettingsModal
import thong.kotlin.pomodoro.core.designsystem.theme.rememberBreathingEffect
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningGroupConfig
import thong.kotlin.pomodoro.features.pomodoro.domain.repository.UserAppStateRepository
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro.domain.model.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.WorkspaceUiState

class PomodoroScreenV2(
    private val soundManager: SoundManager? = null,
    private val repository: UserAppStateRepository? = null,
    private val learningStyle: LearningStyle = LearningStyle.SOLO,
    private val learningGroupConfig: LearningGroupConfig? = null
) : Screen {

    @Composable
    override fun Content() {
        val timerViewModel: TimerViewModel = viewModel { TimerViewModel(soundManager, repository) }
        val tasksViewModel: TasksViewModel = viewModel { TasksViewModel(repository) }
        val workspaceViewModel: WorkspaceViewModel = viewModel { WorkspaceViewModel(soundManager, repository) }

        val workspaceState by workspaceViewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            restoreAudioState(workspaceState, soundManager)
        }
        PomodoroScreenUIv2(timerViewModel, tasksViewModel, workspaceViewModel)
    }
}

private fun restoreAudioState(
    workspaceUiState: WorkspaceUiState,
    soundManager: SoundManager?
) {
    if (soundManager == null) return

    // Phục hồi nhạc nền
    val shouldPlayBackground =
        workspaceUiState.isMusicPlaying && !soundManager.isBackgroundMusicPlaying()
    if (shouldPlayBackground) {
        workspaceUiState.selectedTrackId?.let { trackId ->
            soundManager.playBackgroundMusic(trackId)
        }
    }

    // Phục hồi âm thanh môi trường (Ambient)
    workspaceUiState.activeAmbientSoundIds.forEach { soundId ->
        if (!soundManager.isAmbientSoundPlaying(soundId)) {
            soundManager.playAmbientSound(soundId)
        }
    }
}

@Composable
fun PomodoroScreenUIv2(
    timerViewModel: TimerViewModel,
    tasksViewModel: TasksViewModel,
    workspaceViewModel: WorkspaceViewModel
) {
    val timerState by timerViewModel.uiState.collectAsState()
    val workspaceState by workspaceViewModel.uiState.collectAsState()
    val tasksState by tasksViewModel.uiState.collectAsState()

    val currentBackground =
        workspaceState.availableBackgrounds.find { it.id == workspaceState.selectedBackgroundId }

    AuraBackground(
        imageRes = currentBackground?.resource ?: Res.drawable.startup_bg,
        landscapeImageRes = currentBackground?.landscapeResource,
        blurRadius = if (workspaceState.isCompactMode) 0f else rememberBreathingEffect().blur,
        overlayAlpha = if (workspaceState.isCompactMode) 0.15f else rememberBreathingEffect().alpha
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isLandscape = maxWidth > maxHeight
                if (workspaceState.isCompactMode) {
                    if (isLandscape) {
                        LandscapeCompactUI(
                            workspaceUiState = workspaceState,
                            timerUiState = timerState,
                            tasksUiState = tasksState,
                            onToggleTimer = timerViewModel::toggleTimer,
                            onToggleCompactMode = workspaceViewModel::toggleCompactMode,
                            onToggleCompactMenu = workspaceViewModel::toggleCompactMenu,
                            onSelectCompactSection = workspaceViewModel::setActiveCompactSection,
                            onCloseCompactSection = {
                                workspaceViewModel.setActiveCompactSection(
                                    null
                                )
                            },
                            // Section-specific actions
                            onToggleMusic = workspaceViewModel::toggleMusic,
                            onSelectTrack = workspaceViewModel::selectTrack,
                            onToggleAmbientSound = workspaceViewModel::toggleAmbientSound,
                            onSelectBackground = workspaceViewModel::selectBackground,
                            onAddTask = tasksViewModel::addTask,
                            onDeleteTask = tasksViewModel::deleteTask,
                            onToggleTask = tasksViewModel::toggleTask,
                            onNewTaskTextChange = tasksViewModel::onNewTaskTextChange,
                            onWorkChange = workspaceViewModel::onWorkMinutesChange,
                            onBreakChange = workspaceViewModel::onBreakMinutesChange,
                            onSaveSettings = workspaceViewModel::saveSettings,
                            onResetSettings = workspaceViewModel::resetSettingsToDefault
                        )
                    } else {
                        PortraitCompactUI(
                            workspaceUiState = workspaceState,
                            timerUiState = timerState,
                            tasksUiState = tasksState,
                            onToggleTimer = timerViewModel::toggleTimer,
                            onToggleCompactMode = workspaceViewModel::toggleCompactMode,
                            onToggleCompactMenu = workspaceViewModel::toggleCompactMenu,
                            onSelectCompactSection = workspaceViewModel::setActiveCompactSection,
                            onCloseCompactSection = {
                                workspaceViewModel.setActiveCompactSection(
                                    null
                                )
                            },
                            // Section-specific actions
                            onToggleMusic = workspaceViewModel::toggleMusic,
                            onSelectTrack = workspaceViewModel::selectTrack,
                            onToggleAmbientSound = workspaceViewModel::toggleAmbientSound,
                            onSelectBackground = workspaceViewModel::selectBackground,
                            onAddTask = tasksViewModel::addTask,
                            onDeleteTask = tasksViewModel::deleteTask,
                            onToggleTask = tasksViewModel::toggleTask,
                            onNewTaskTextChange = tasksViewModel::onNewTaskTextChange,
                            onWorkChange = workspaceViewModel::onWorkMinutesChange,
                            onBreakChange = workspaceViewModel::onBreakMinutesChange,
                            onSaveSettings = workspaceViewModel::saveSettings,
                            onResetSettings = workspaceViewModel::resetSettingsToDefault
                        )
                    }
                } else {
                    if (isLandscape) {
                        LandscapePomodoroUI(
                            workspaceUiState = workspaceState,
                            timerUiState = timerState,
                            tasksUiState = tasksState,
                            themeColor = rememberPomodoroThemeColor(workspaceState.currentMode),
                            onToggleTimer = timerViewModel::toggleTimer,
                            onResetTimer = timerViewModel::resetTimer,
                            onSkipTimer = timerViewModel::skipTimer,
                            onToggleSettings = workspaceViewModel::toggleSettings,
                            onToggleCompactMode = workspaceViewModel::toggleCompactMode,
                            onToggleMusic = workspaceViewModel::toggleMusic,
                            onSelectTrack = workspaceViewModel::selectTrack,
                            onToggleAmbientSound = workspaceViewModel::toggleAmbientSound,
                            onSelectBackground = workspaceViewModel::selectBackground,
                            onAddTask = tasksViewModel::addTask,
                            onDeleteTask = tasksViewModel::deleteTask,
                            onToggleTask = tasksViewModel::toggleTask,
                            onNewTaskTextChange = tasksViewModel::onNewTaskTextChange,
                            onToggleTasksExpanded = tasksViewModel::toggleTasksExpanded
                        )
                    } else {
                        PortraitPomodoroUI(
                            workspaceUiState = workspaceState,
                            timerUiState = timerState,
                            tasksUiState = tasksState,
                            themeColor = rememberPomodoroThemeColor(workspaceState.currentMode),
                            onToggleTimer = timerViewModel::toggleTimer,
                            onResetTimer = timerViewModel::resetTimer,
                            onSkipTimer = timerViewModel::skipTimer,
                            onToggleSettings = workspaceViewModel::toggleSettings,
                            onToggleCompactMode = workspaceViewModel::toggleCompactMode,
                            onAddTask = tasksViewModel::addTask,
                            onDeleteTask = tasksViewModel::deleteTask,
                            onToggleTasksExpanded = tasksViewModel::toggleTasksExpanded,
                            onToggleTask = tasksViewModel::toggleTask,
                            onNewTaskTextChange = tasksViewModel::onNewTaskTextChange,
                            onToggleMusic = workspaceViewModel::toggleMusic,
                            onSelectTrack = workspaceViewModel::selectTrack,
                            onToggleAmbientSound = workspaceViewModel::toggleAmbientSound,
                            onSelectBackground = workspaceViewModel::selectBackground
                        )
                    }
                }
            }

            // Settings Modal
            if (workspaceState.isSettingsVisible) {
                PomodoroSettingsModal(
                    workspaceUiState = workspaceState,
                    onWorkChange = workspaceViewModel::onWorkMinutesChange,
                    onBreakChange = workspaceViewModel::onBreakMinutesChange,
                    onSave = workspaceViewModel::saveSettings,
                    onCancel = workspaceViewModel::toggleSettings,
                    onReset = workspaceViewModel::resetSettingsToDefault
                )
            }
        }
    }
}

@Composable
fun rememberPomodoroThemeColor(currentMode: PomodoroMode): Color {
    val animatedThemeColor by animateColorAsState(
        targetValue = when (currentMode) {
            PomodoroMode.WORK -> AuraColors.WorkMode
            PomodoroMode.SHORT_BREAK -> AuraColors.ShortBreakMode
            PomodoroMode.LONG_BREAK -> AuraColors.LongBreakMode
        },
        animationSpec = tween(durationMillis = 500),
        label = "ThemeColorTransition",
    )
    return animatedThemeColor
}