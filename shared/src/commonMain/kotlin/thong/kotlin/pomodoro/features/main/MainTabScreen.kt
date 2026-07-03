package thong.kotlin.pomodoro.features.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningGroupConfig
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro._base.PomodoroScreenUIv2
import thong.kotlin.pomodoro.features.pomodoro.viewmodel.AppViewModel
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.streak.presentation.StreakScreenContent
import thong.kotlin.pomodoro.features.streak.presentation.StreakViewModel

class MainTabScreen(
    private val currentSession: LearningSessionRecord,
    private val learningStyle: LearningStyle = LearningStyle.SOLO,
    private val learningGroupConfig: LearningGroupConfig? = null
) : Screen {

    @Composable
    override fun Content() {
        var selectedTab by remember { mutableStateOf(0) }
        val navigator = LocalNavigator.currentOrThrow
        val soundManager = DependencyRegistry.soundManager

        val appViewModel: AppViewModel = viewModel(
            key = "AppViewModel_${currentSession.sessionId}"
        ) {
            AppViewModel(
                currentSession = currentSession,
                soundManager = soundManager
            )
        }
        val streakVM = viewModel { StreakViewModel(DependencyRegistry.streakRepository) }

        Scaffold(
            containerColor = AuraColors.Background,
            bottomBar = {
                NavigationBar(
                    containerColor = AuraColors.surface.copy(alpha = 0.7f),
                    contentColor = AuraColors.TextPrimary
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
                    0 -> PomodoroScreenUIv2(
                        appViewModel = appViewModel,
                        learningStyle = learningStyle,
                        learningGroupConfig = learningGroupConfig,
                        soundManager = soundManager,
                        navigator = navigator
                    )
                    1 -> {
                        val uiState by streakVM.uiState.collectAsState()
                        StreakScreenContent(uiState = uiState)
                    }
                    2 -> Box(modifier = Modifier.fillMaxSize()) // Settings placeholder
                }
            }
        }
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = AuraColors.primary,
    selectedTextColor = AuraColors.primary,
    unselectedIconColor = AuraColors.TextSecondary,
    unselectedTextColor = AuraColors.TextSecondary,
    indicatorColor = AuraColors.primary.copy(alpha = 0.15f)
)
