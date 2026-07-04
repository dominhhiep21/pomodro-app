package thong.kotlin.pomodoro.features.pomodoro._base

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.startup_bg
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.designsystem.theme.rememberBreathingEffect
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.focus.score.presentation.FocusScoreCard
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningGroupConfig
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro._base.components.LandscapeCompactUI
import thong.kotlin.pomodoro.features.pomodoro._base.components.LandscapePomodoroGroupUI
import thong.kotlin.pomodoro.features.pomodoro._base.components.LandscapePomodoroUI
import thong.kotlin.pomodoro.features.pomodoro._base.components.PortraitCompactUI
import thong.kotlin.pomodoro.features.pomodoro._base.components.PortraitPomodoroGroupUI
import thong.kotlin.pomodoro.features.pomodoro._base.components.PortraitPomodoroUI
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.PomodoroUiState
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettingsV2
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.ExitConfirmationModal
import thong.kotlin.pomodoro.features.pomodoro.timer.presentation.components.PomodoroSettingsModal
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.AppViewModel
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.TotallyPomodoroUiState
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.presentation.SessionHistoryScreen

class PomodoroScreenV2(
    private val learningStyle: LearningStyle = LearningStyle.SOLO,
    private val learningGroupConfig: LearningGroupConfig? = null,
    private val currentSessionId: String,
    private val isNewSession: Boolean
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val soundManager = DependencyRegistry.soundManager
        val learningSessionManager = DependencyRegistry.learningSessionManager
        val userSettings: UserSettingsV2 = DependencyRegistry.userAppStateRepositoryV2.getUserSettings()

        val currentSession by produceState<LearningSessionRecord?>(
            initialValue = null,
            key1 = currentSessionId
        ) {
            value = learningSessionManager.getSessionById(currentSessionId)
        }

        val session = currentSession ?: return

        val newCurrentSession: LearningSessionRecord = if (isNewSession) {
            session.copy(
                plannedWorkMinutes = userSettings.personalWorkMinutes,
                plannedBreakMinutes = userSettings.personalBreakMinutes,
                plannedLongBreakMinutes = userSettings.personalLongBreakMinutes,
                lastBackgroundId = userSettings.personalSelectedBackgroundId,
                lastMusicId = userSettings.personalLastSelectedMusicId
            )
        } else {
            session.copy()
        }

        LaunchedEffect(Unit) {
            learningSessionManager.updateSession(newCurrentSession)
        }

        val appViewModel: AppViewModel = viewModel(
            key = "AppViewModel_$currentSessionId"
        ) {
            AppViewModel(
                currentSession = newCurrentSession,
                isNewSession = isNewSession,
                soundManager = soundManager
            )
        }


        val appState by appViewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            restoreAudioState(appState, soundManager)
        }
        PomodoroScreenUIv2(
            appViewModel = appViewModel,
            learningStyle = learningStyle,
            learningGroupConfig = learningGroupConfig,
            soundManager = soundManager,
            navigator = navigator
        )
    }
}

private fun restoreAudioState(
    totallyPomodoroUiState: TotallyPomodoroUiState,
    soundManager: SoundManager?
) {
    if (soundManager == null) return

    // Phục hồi nhạc nền
    val shouldPlayBackground =
        totallyPomodoroUiState.workspaceUiState.isMusicPlaying && !soundManager.isBackgroundMusicPlaying()
    if (shouldPlayBackground) {
        totallyPomodoroUiState.workspaceUiState.selectedTrackId?.let { trackId ->
            soundManager.playBackgroundMusic(trackId)
        }
    }

    // Phục hồi âm thanh môi trường (Ambient)
    totallyPomodoroUiState.workspaceUiState.activeAmbientSoundIds.forEach { soundId ->
        if (!soundManager.isAmbientSoundPlaying(soundId)) {
            soundManager.playAmbientSound(soundId)
        }
    }
}

@Composable
fun PomodoroScreenUIv2(
    appViewModel: AppViewModel,
    learningStyle: LearningStyle = LearningStyle.SOLO,
    learningGroupConfig: LearningGroupConfig? = null,
    soundManager: SoundManager? = DependencyRegistry.soundManager,
    navigator: Navigator
) {
    val totalPomodoroUiState by appViewModel.uiState.collectAsState()

    val currentBackground =
        totalPomodoroUiState.workspaceUiState.availableBackgrounds
            .find { it.id == totalPomodoroUiState.workspaceUiState.selectedBackgroundId }

    AuraBackground(
        imageRes = currentBackground?.resource ?: Res.drawable.startup_bg,
        landscapeImageRes = currentBackground?.landscapeResource,
        blurRadius = if (totalPomodoroUiState.workspaceUiState.isCompactMode) 0f else rememberBreathingEffect().blur,
        overlayAlpha = if (totalPomodoroUiState.workspaceUiState.isCompactMode) 0.15f else rememberBreathingEffect().alpha
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isLandscape = maxWidth > maxHeight
                val currentState = PomodoroUiState(totalPomodoroUiState.workspaceUiState.isCompactMode, isLandscape, learningStyle)
                when (currentState) {
                    // Gen UI Landscape Compact Group
                    PomodoroUiState(isCompact = true, isLandscape = true, style = LearningStyle.GROUP) -> {
                        LandscapeCompactUI(
                            totallyPomodoroUiState = totalPomodoroUiState,
                            learningStyle = learningStyle,
                            onToggleTimer = appViewModel::toggleTimer,
                            onToggleCompactMode = appViewModel::toggleCompactMode,
                            onToggleCompactMenu = appViewModel::toggleCompactMenu,
                            onSelectCompactSection = appViewModel::setActiveCompactSection,
                            onCloseCompactSection = {
                                appViewModel.setActiveCompactSection(
                                    null
                                )
                            },
                            // Section-specific actions
                            onToggleMusic = appViewModel::toggleMusic,
                            onSelectTrack = appViewModel::selectTrack,
                            onToggleAmbientSound = appViewModel::toggleAmbientSound,
                            onSelectBackground = appViewModel::selectBackground,
                            onAddTask = appViewModel::addTask,
                            onDeleteTask = appViewModel::deleteTask,
                            onToggleTask = appViewModel::toggleTask,
                            onNewTaskTextChange = appViewModel::onNewTaskTextChange,
                            onWorkChange = appViewModel::onWorkMinutesChange,
                            onBreakChange = appViewModel::onBreakMinutesChange,
                            onSaveSettings = {
                                appViewModel.saveSettings { work, breakTime ->
                                    appViewModel.updateConfig(
                                        totalPomodoroUiState.timerUiState.config.copy(
                                            workMinutes = work,
                                            shortBreakMinutes = breakTime
                                        )
                                    )
                                }
                            },
                            onResetSettings = appViewModel::resetSettingsToDefault,
                            onToggleSettings = appViewModel::toggleSettings,
                            onExit = appViewModel::toggleExitModal
                        )
                    }
                    // Gen UI Landscape Compact Solo
                    PomodoroUiState(isCompact = true, isLandscape = true, style = LearningStyle.SOLO) -> {
                        LandscapeCompactUI(
                            totallyPomodoroUiState = totalPomodoroUiState,
                            onToggleTimer = appViewModel::toggleTimer,
                            onToggleCompactMode = appViewModel::toggleCompactMode,
                            onToggleCompactMenu = appViewModel::toggleCompactMenu,
                            onSelectCompactSection = appViewModel::setActiveCompactSection,
                            onCloseCompactSection = {
                                appViewModel.setActiveCompactSection(
                                    null
                                )
                            },
                            // Section-specific actions
                            onToggleMusic = appViewModel::toggleMusic,
                            onSelectTrack = appViewModel::selectTrack,
                            onToggleAmbientSound = appViewModel::toggleAmbientSound,
                            onSelectBackground = appViewModel::selectBackground,
                            onAddTask = appViewModel::addTask,
                            onDeleteTask = appViewModel::deleteTask,
                            onToggleTask = appViewModel::toggleTask,
                            onNewTaskTextChange = appViewModel::onNewTaskTextChange,
                            onWorkChange = appViewModel::onWorkMinutesChange,
                            onBreakChange = appViewModel::onBreakMinutesChange,
                            onSaveSettings = {
                                appViewModel.saveSettings { work, breakTime ->
                                    appViewModel.updateConfig(
                                        totalPomodoroUiState.timerUiState.config.copy(
                                            workMinutes = work,
                                            shortBreakMinutes = breakTime
                                        )
                                    )
                                }
                            },
                            onResetSettings = appViewModel::resetSettingsToDefault,
                            onToggleSettings = appViewModel::toggleSettings,
                            onExit = appViewModel::toggleExitModal
                        )
                    }
                    // Gen UI Portrait Compact Group
                    PomodoroUiState(isCompact = true, isLandscape = false, style = LearningStyle.GROUP) -> {
                        PortraitCompactUI(
                            totallyPomodoroUiState = totalPomodoroUiState,
                            learningStyle = learningStyle,
                            onToggleSettings = appViewModel::toggleSettings,
                            onToggleTimer = appViewModel::toggleTimer,
                            onToggleCompactMode = appViewModel::toggleCompactMode,
                            onToggleCompactMenu = appViewModel::toggleCompactMenu,
                            onSelectCompactSection = appViewModel::setActiveCompactSection,
                            onCloseCompactSection = {
                                appViewModel.setActiveCompactSection(
                                    null
                                )
                            },
                            // Section-specific actions
                            onToggleMusic = appViewModel::toggleMusic,
                            onSelectTrack = appViewModel::selectTrack,
                            onToggleAmbientSound = appViewModel::toggleAmbientSound,
                            onSelectBackground = appViewModel::selectBackground,
                            onAddTask = appViewModel::addTask,
                            onDeleteTask = appViewModel::deleteTask,
                            onToggleTask = appViewModel::toggleTask,
                            onNewTaskTextChange = appViewModel::onNewTaskTextChange,
                            onWorkChange = appViewModel::onWorkMinutesChange,
                            onBreakChange = appViewModel::onBreakMinutesChange,
                            onSaveSettings = {
                                appViewModel.saveSettings { work, breakTime ->
                                    appViewModel.updateConfig(
                                        totalPomodoroUiState.timerUiState.config.copy(
                                            workMinutes = work,
                                            shortBreakMinutes = breakTime
                                        )
                                    )
                                }
                            },
                            onResetSettings = appViewModel::resetSettingsToDefault,
                            onExit = appViewModel::toggleExitModal
                        )
                    }
                    // Gen UI Portrait Compact Solo
                    PomodoroUiState(isCompact = true, isLandscape = false, style = LearningStyle.SOLO) -> {
                        PortraitCompactUI(
                            totallyPomodoroUiState = totalPomodoroUiState,
                            onToggleTimer = appViewModel::toggleTimer,
                            onToggleCompactMode = appViewModel::toggleCompactMode,
                            onToggleCompactMenu = appViewModel::toggleCompactMenu,
                            onSelectCompactSection = appViewModel::setActiveCompactSection,
                            onCloseCompactSection = {
                                appViewModel.setActiveCompactSection(
                                    null
                                )
                            },
                            // Section-specific actions
                            onToggleMusic = appViewModel::toggleMusic,
                            onSelectTrack = appViewModel::selectTrack,
                            onToggleAmbientSound = appViewModel::toggleAmbientSound,
                            onSelectBackground = appViewModel::selectBackground,
                            onAddTask = appViewModel::addTask,
                            onDeleteTask = appViewModel::deleteTask,
                            onToggleTask = appViewModel::toggleTask,
                            onNewTaskTextChange = appViewModel::onNewTaskTextChange,
                            onWorkChange = appViewModel::onWorkMinutesChange,
                            onBreakChange = appViewModel::onBreakMinutesChange,
                            onSaveSettings = {
                                appViewModel.saveSettings { work, breakTime ->
                                    appViewModel.updateConfig(
                                        totalPomodoroUiState.timerUiState.config.copy(
                                            workMinutes = work,
                                            shortBreakMinutes = breakTime
                                        )
                                    )
                                }
                            },
                            onResetSettings = appViewModel::resetSettingsToDefault,
                            onToggleSettings = appViewModel::toggleSettings,
                            onExit = appViewModel::toggleExitModal
                        )
                    }
                    // Gen UI Landscape Group
                    PomodoroUiState(isCompact = false, isLandscape = true, style = LearningStyle.GROUP) -> {
                        LandscapePomodoroGroupUI(
                            totallyPomodoroUiState = totalPomodoroUiState,
                            groupConfig = learningGroupConfig ?: LearningGroupConfig(),
                            themeColor = rememberPomodoroThemeColor(totalPomodoroUiState.currentMode),
                            onToggleTimer = appViewModel::toggleTimer,
                            onResetTimer = appViewModel::resetTimer,
                            onSkipTimer = appViewModel::skipTimer,
                            onToggleSettings = appViewModel::toggleSettings,
                            onToggleCompactMode = appViewModel::toggleCompactMode,
                            onToggleMusic = appViewModel::toggleMusic,
                            onSelectTrack = appViewModel::selectTrack,
                            onToggleAmbientSound = appViewModel::toggleAmbientSound,
                            onSelectBackground = appViewModel::selectBackground,
                            onAddTask = appViewModel::addTask,
                            onDeleteTask = appViewModel::deleteTask,
                            onToggleTask = appViewModel::toggleTask,
                            onNewTaskTextChange = appViewModel::onNewTaskTextChange,
                            onToggleTasksExpanded = appViewModel::toggleTasksExpanded,
                            onExit = appViewModel::toggleExitModal
                        )
                    }
                    // Gen UI Landscape Solo
                    PomodoroUiState(isCompact = false, isLandscape = true, style = LearningStyle.SOLO) -> {
                        LandscapePomodoroUI(
                            totallyPomodoroUiState = totalPomodoroUiState,
                            themeColor = rememberPomodoroThemeColor(totalPomodoroUiState.currentMode),
                            onToggleTimer = appViewModel::toggleTimer,
                            onResetTimer = appViewModel::resetTimer,
                            onSkipTimer = appViewModel::skipTimer,
                            onToggleSettings = appViewModel::toggleSettings,
                            onToggleCompactMode = appViewModel::toggleCompactMode,
                            onToggleMusic = appViewModel::toggleMusic,
                            onSelectTrack = appViewModel::selectTrack,
                            onToggleAmbientSound = appViewModel::toggleAmbientSound,
                            onSelectBackground = appViewModel::selectBackground,
                            onAddTask = appViewModel::addTask,
                            onDeleteTask = appViewModel::deleteTask,
                            onToggleTask = appViewModel::toggleTask,
                            onNewTaskTextChange = appViewModel::onNewTaskTextChange,
                            onToggleTasksExpanded = appViewModel::toggleTasksExpanded,
                            onExit = appViewModel::toggleExitModal
                        )
                    }
                    // Gen UI Portrait Group
                    PomodoroUiState(isCompact = false, isLandscape = false, style = LearningStyle.GROUP) -> {
                        PortraitPomodoroGroupUI(
                            totallyPomodoroUiState = totalPomodoroUiState,
                            groupConfig = learningGroupConfig ?: LearningGroupConfig(),
                            themeColor = rememberPomodoroThemeColor(totalPomodoroUiState.currentMode),
                            onToggleTimer = appViewModel::toggleTimer,
                            onResetTimer = appViewModel::resetTimer,
                            onSkipTimer = appViewModel::skipTimer,
                            onToggleSettings = appViewModel::toggleSettings,
                            onToggleCompactMode = appViewModel::toggleCompactMode,
                            onAddTask = appViewModel::addTask,
                            onDeleteTask = appViewModel::deleteTask,
                            onToggleTasksExpanded = appViewModel::toggleTasksExpanded,
                            onToggleTask = appViewModel::toggleTask,
                            onNewTaskTextChange = appViewModel::onNewTaskTextChange,
                            onToggleMusic = appViewModel::toggleMusic,
                            onSelectTrack = appViewModel::selectTrack,
                            onToggleAmbientSound = appViewModel::toggleAmbientSound,
                            onSelectBackground = appViewModel::selectBackground,
                            onExit = appViewModel::toggleExitModal
                        )
                    }
                    // Gen UI Portrait Solo
                    PomodoroUiState(isCompact = false, isLandscape = false, style = LearningStyle.SOLO) -> {
                        PortraitPomodoroUI(
                            totallyPomodoroUiState = totalPomodoroUiState,
                            themeColor = rememberPomodoroThemeColor(totalPomodoroUiState.currentMode),
                            onToggleTimer = appViewModel::toggleTimer,
                            onResetTimer = appViewModel::resetTimer,
                            onSkipTimer = appViewModel::skipTimer,
                            onToggleSettings = appViewModel::toggleSettings,
                            onToggleCompactMode = appViewModel::toggleCompactMode,
                            onAddTask = appViewModel::addTask,
                            onDeleteTask = appViewModel::deleteTask,
                            onToggleTasksExpanded = appViewModel::toggleTasksExpanded,
                            onToggleTask = appViewModel::toggleTask,
                            onNewTaskTextChange = appViewModel::onNewTaskTextChange,
                            onToggleMusic = appViewModel::toggleMusic,
                            onSelectTrack = appViewModel::selectTrack,
                            onToggleAmbientSound = appViewModel::toggleAmbientSound,
                            onSelectBackground = appViewModel::selectBackground,
                            onExit = appViewModel::toggleExitModal
                        )
                    }
                }
            }

            // Settings Modal
            if (totalPomodoroUiState.workspaceUiState.isSettingsVisible) {
                PomodoroSettingsModal(
                    totallyPomodoroUiState = totalPomodoroUiState,
                    onWorkChange = appViewModel::onWorkMinutesChange,
                    onBreakChange = appViewModel::onBreakMinutesChange,
                    onSave = {
                        appViewModel.saveSettings { work, breakTime ->
                            appViewModel.updateConfig(
                                totalPomodoroUiState.timerUiState.config.copy(
                                    workMinutes = work,
                                    shortBreakMinutes = breakTime
                                )
                            )
                        }
                    },
                    onCancel = appViewModel::toggleSettings,
                    onReset = appViewModel::resetSettingsToDefault
                )
            }

            // Exit Confirmation Modal
            if (totalPomodoroUiState.workspaceUiState.isExitModalVisible) {
                ExitConfirmationModal(
                    onDismiss = appViewModel::toggleExitModal,
                    onEndSession = {
                        appViewModel.endSession {
                            soundManager?.stopAllSounds()
                            navigator.replace(SessionHistoryScreen())
                        }
                    },
                    onPauseSession = {
                        appViewModel.pauseSession {
                            soundManager?.stopAllSounds()
                            navigator.replace(SessionHistoryScreen())
                        }
                    },
                    onDeleteSession = {
                        appViewModel.deleteSession {
                            soundManager?.stopAllSounds()
                            navigator.replace(SessionHistoryScreen())
                        }
                    }
                )
            }

            // Focus Score Modal
            totalPomodoroUiState.workspaceUiState.focusScoreResult?.let { result ->
                Dialog(
                    onDismissRequest = {
                        appViewModel.dismissFocusScore {
                            soundManager?.stopAllSounds()
                            navigator.replace(SessionHistoryScreen())
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FocusScoreCard(result = result)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        AuraButton(
                            onClick = {
                                appViewModel.dismissFocusScore {
                                    soundManager?.stopAllSounds()
                                    navigator.replace(SessionHistoryScreen())
                                }
                            }
                        ) {
                            Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
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
