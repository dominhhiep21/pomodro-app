package thong.kotlin.pomodoro.features.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningGroupConfig
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2
import thong.kotlin.pomodoro.features.streak.presentation.StreakScreen

class MainTabScreen(
    private val soundManager: SoundManager? = null,
    private val repository: UserAppStateRepositoryV2,
    private val learningStyle: LearningStyle = LearningStyle.SOLO,
    private val learningGroupConfig: LearningGroupConfig? = null
) : Screen {

    @Composable
    override fun Content() {
        var selectedTab by remember { mutableStateOf(0) }

        Scaffold(
            containerColor = AuraColors.background,
            bottomBar = {
                NavigationBar(
                    containerColor = AuraColors.surface.copy(alpha = 0.7f),
                    contentColor = AuraColors.textPrimary
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Timer, contentDescription = "Timer") },
                        label = { Text("Timer") },
                        colors = navItemColors()
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Pets, contentDescription = "Streak") },
                        label = { Text("Streak") },
                        colors = navItemColors()
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = navItemColors()
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (selectedTab) {
                    0 -> TimerTabContent(soundManager, repository, learningStyle, learningGroupConfig)
                    1 -> StreakTabContent()
                    2 -> SettingsTabContent()
                }
            }
        }
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = AuraColors.primary,
    selectedTextColor = AuraColors.primary,
    unselectedIconColor = AuraColors.textSecondary,
    unselectedTextColor = AuraColors.textSecondary,
    indicatorColor = AuraColors.primary.copy(alpha = 0.15f)
)

@Composable
private fun TimerTabContent(
    soundManager: SoundManager?,
    repository: UserAppStateRepositoryV2,
    learningStyle: LearningStyle,
    learningGroupConfig: LearningGroupConfig?
) {
    // Reuse existing PomodoroScreenV2 content inline
    val timerVM = androidx.lifecycle.viewmodel.compose.viewModel {
        thong.kotlin.pomodoro.features.pomodoro.viewmodel.TimerViewModel(soundManager, repository)
    }
    val tasksVM = androidx.lifecycle.viewmodel.compose.viewModel {
        thong.kotlin.pomodoro.features.pomodoro.viewmodel.TasksViewModel(repository)
    }
    val workspaceVM = androidx.lifecycle.viewmodel.compose.viewModel {
        thong.kotlin.pomodoro.features.pomodoro.viewmodel.WorkspaceViewModel(soundManager, repository)
    }
    thong.kotlin.pomodoro.features.pomodoro._base.PomodoroScreenUIv2(
        soundManager, timerVM, tasksVM, workspaceVM, learningStyle, learningGroupConfig, null
    )
}

@Composable
private fun StreakTabContent() {
    val streakRepo = remember { DependencyRegistry.streakRepository }
    val vm = androidx.lifecycle.viewmodel.compose.viewModel {
        thong.kotlin.pomodoro.features.streak.presentation.StreakViewModel(streakRepo)
    }
    val uiState by vm.uiState.collectAsState()
    thong.kotlin.pomodoro.features.streak.presentation.StreakScreenContent(
        uiState = uiState,
        petState = vm.getPetState()
    )
}

@Composable
private fun SettingsTabContent() {
    // Placeholder - existing settings can be integrated here
    Box(modifier = Modifier.fillMaxSize())
}
