package thong.kotlin.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import thong.kotlin.pomodoro.core.designsystem.theme.AuraTheme
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.core.notification.NotificationManager
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.pomodoro.timer.state.PomodoroUiState
import thong.kotlin.pomodoro.features.pomodoro.timer.state.PomodoroUiStateSaver
import thong.kotlin.pomodoro.features.pomodoro.timer.viewmodel.PomodoroViewModel
import thong.kotlin.pomodoro.features.startup.presentation.StartupLoadingScreen

@Composable
@Preview
fun App2(
    soundManager: SoundManager? = null,
    notificationManager: NotificationManager? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { DependencyRegistry.userAppStateRepository }

    // 2. KHỞI TẠO STATE & VIEWMODEL (Pomodoro)
    var savedPomodoroState by rememberSaveable(stateSaver = PomodoroUiStateSaver) {
        mutableStateOf(PomodoroUiState())
    }

    val pomodoroViewModel = remember {
        PomodoroViewModel(
            viewModelScope = coroutineScope,
            soundManager = soundManager,
            repository = repository,
            initialState = savedPomodoroState
        )
    }

    // 3. XỬ LÝ SIDE EFFECTS (Phục hồi Audio & Đồng bộ State)
    LaunchedEffect(Unit) {
        restoreAudioState(savedPomodoroState, soundManager)

        // Cập nhật State từ ViewModel
        pomodoroViewModel.uiState.collect { newState ->
            savedPomodoroState = newState
        }
    }

    // 4. RENDER GIAO DIỆN
    AuraTheme {
        Navigator(screen = StartupLoadingScreen(notificationManager))
    }
}

private fun restoreAudioState(
    savedState: PomodoroUiState,
    soundManager: SoundManager?
) {
    if (soundManager == null) return

    // Phục hồi nhạc nền
    val shouldPlayBackground = savedState.isMusicPlaying && !soundManager.isBackgroundMusicPlaying()
    if (shouldPlayBackground) {
        savedState.selectedTrackId?.let { trackId ->
            soundManager.playBackgroundMusic(trackId)
        }
    }

    // Phục hồi âm thanh môi trường (Ambient)
    savedState.activeAmbientSoundIds.forEach { soundId ->
        if (!soundManager.isAmbientSoundPlaying(soundId)) {
            soundManager.playAmbientSound(soundId)
        }
    }
}