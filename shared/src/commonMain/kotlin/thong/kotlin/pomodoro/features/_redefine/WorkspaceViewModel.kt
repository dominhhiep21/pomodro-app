package thong.kotlin.pomodoro.features._redefine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.features.pomodoro.ambient.data.AmbientSoundRepository
import thong.kotlin.pomodoro.features.pomodoro.ambient.domain.AmbientSound
import thong.kotlin.pomodoro.features.pomodoro.domain.model.UserSettings
import thong.kotlin.pomodoro.features.pomodoro.domain.repository.UserAppStateRepository
import thong.kotlin.pomodoro.features.pomodoro.music.data.MusicRepository
import thong.kotlin.pomodoro.features.pomodoro.music.domain.MusicTrack
import thong.kotlin.pomodoro.features.pomodoro.timer.state.CompactSection
import thong.kotlin.pomodoro.features.settings.data.BackgroundRepository
import thong.kotlin.pomodoro.features.settings.domain.AppBackground

data class WorkspaceUiState(
    // Background
    val selectedBackgroundId: String = BackgroundRepository.DEFAULT_BACKGROUND_ID,
    val availableBackgrounds: List<AppBackground> = emptyList(), // Sử dụng AppBackground
    val backgroundConfig: thong.kotlin.pomodoro.features.background.model.BackgroundConfig? = null,

    // Music
    val isMusicPlaying: Boolean = false,
    val selectedTrackId: String? = MusicRepository.DEFAULT_TRACK_ID,
    val availableTracks: List<MusicTrack> = emptyList(), // Sử dụng MusicTrack
    val musicPosition: Long = 0L,

    // Ambient Sounds
    val activeAmbientSoundIds: Set<String> = emptySet(),
    val availableAmbientSounds: List<AmbientSound> = emptyList(), // Sử dụng AmbientSound

    // UI Modes & Toggles
    val isCompactMode: Boolean = false,
    val isCompactMenuExpanded: Boolean = false,
    val activeCompactSection: CompactSection? = null,
    val isNotificationEnabled: Boolean = false,

    // Settings State
    val isSettingsVisible: Boolean = false,
    val editingWorkMinutes: String = "25",
    val editingBreakMinutes: String = "5",
    val settingsError: String? = null
)
class WorkspaceViewModel(
    private val soundManager: SoundManager? = null,
    private val repository: UserAppStateRepository? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkspaceUiState())
    val uiState: StateFlow<WorkspaceUiState> = _uiState.asStateFlow()

    init {
        loadWorkspaceSettings()
    }

    private fun loadWorkspaceSettings() {
        viewModelScope.launch {
            // Load danh sách dữ liệu tĩnh từ 3 Object Repositories của bạn
            _uiState.update { state ->
                state.copy(
                    availableTracks = MusicRepository.availableTracks,
                    availableAmbientSounds = AmbientSoundRepository.availableSounds,
                    availableBackgrounds = BackgroundRepository.availableBackgrounds
                )
            }

            // Lắng nghe dữ liệu cấu hình đã lưu (Database/DataStore)
            repository?.getSettingsFlow()?.collect { settings ->
                _uiState.update { state ->
                    state.copy(
                        selectedBackgroundId = settings.selectedBackgroundId ?: BackgroundRepository.DEFAULT_BACKGROUND_ID,
                        isNotificationEnabled = settings.isNotificationEnabled,
                        isCompactMode = settings.isCompactMode,
                        // Nếu trong tương lai settings có lưu bài nhạc cuối cùng nghe, cập nhật tại đây:
                        // selectedTrackId = settings.selectedTrackId ?: MusicRepository.DEFAULT_TRACK_ID
                    )
                }
            }
        }
    }

    // --- MUSIC & AUDIO ---
    fun toggleMusic() {
        _uiState.update { state ->
            val newIsPlaying = !state.isMusicPlaying
            if (newIsPlaying) {
                // Sử dụng default track nếu chưa có track nào được chọn
                val trackToPlay = state.selectedTrackId ?: MusicRepository.DEFAULT_TRACK_ID
                soundManager?.playBackgroundMusic(trackToPlay)
            } else {
                soundManager?.pauseBackgroundMusic()
            }
            state.copy(
                isMusicPlaying = newIsPlaying,
                musicPosition = soundManager?.getCurrentPosition() ?: 0L
            )
        }
    }

    fun selectTrack(trackId: String) {
        _uiState.update { state ->
            if (state.isMusicPlaying) {
                soundManager?.playBackgroundMusic(trackId)
            }
            state.copy(
                selectedTrackId = trackId,
                musicPosition = 0L
            )
        }
    }

    fun toggleAmbientSound(soundId: String) {
        _uiState.update { state ->
            val isCurrentlyActive = state.activeAmbientSoundIds.contains(soundId)
            val newActiveIds = if (isCurrentlyActive) {
                soundManager?.stopAmbientSound(soundId)
                state.activeAmbientSoundIds - soundId
            } else {
                soundManager?.playAmbientSound(soundId)
                state.activeAmbientSoundIds + soundId
            }
            state.copy(activeAmbientSoundIds = newActiveIds)
        }
    }

    // --- BACKGROUND ---
    fun selectBackground(backgroundId: String) {
        _uiState.update { it.copy(selectedBackgroundId = backgroundId) }
        viewModelScope.launch {
            repository?.let { repo ->
                repo.saveUserSettings(repo.getUserSettings().copy(selectedBackgroundId = backgroundId))
            }
        }
    }

    fun updateBackgroundConfig(config: thong.kotlin.pomodoro.features.background.model.BackgroundConfig) {
        _uiState.update { it.copy(backgroundConfig = config) }
    }

    // --- UI MODES ---
    fun toggleCompactMode() {
        _uiState.update { state ->
            val newValue = !state.isCompactMode
            viewModelScope.launch {
                repository?.let { repo ->
                    repo.saveUserSettings(repo.getUserSettings().copy(isCompactMode = newValue))
                }
            }
            state.copy(isCompactMode = newValue, isCompactMenuExpanded = false)
        }
    }

    fun toggleCompactMenu() {
        _uiState.update { it.copy(isCompactMenuExpanded = !it.isCompactMenuExpanded) }
    }

    fun setActiveCompactSection(section: CompactSection?) {
        _uiState.update { it.copy(activeCompactSection = section, isCompactMenuExpanded = false) }
    }

    fun toggleNotificationEnabled() {
        _uiState.update { state ->
            val newValue = !state.isNotificationEnabled
            viewModelScope.launch {
                repository?.let { repo ->
                    repo.saveUserSettings(repo.getUserSettings().copy(isNotificationEnabled = newValue))
                }
            }
            state.copy(isNotificationEnabled = newValue)
        }
    }

    // --- SETTINGS FORM ---
    fun toggleSettings() {
        _uiState.update { state ->
            if (!state.isSettingsVisible) {
                viewModelScope.launch {
                    val currentSettings = repository?.getUserSettings() ?: UserSettings()
                    _uiState.update {
                        it.copy(
                            isSettingsVisible = true,
                            editingWorkMinutes = currentSettings.workMinutes.toString(),
                            editingBreakMinutes = currentSettings.breakMinutes.toString(),
                            settingsError = null
                        )
                    }
                }
                state.copy(isSettingsVisible = true)
            } else {
                state.copy(isSettingsVisible = false)
            }
        }
    }

    fun onWorkMinutesChange(value: String) {
        _uiState.update { it.copy(editingWorkMinutes = value, settingsError = null) }
    }

    fun onBreakMinutesChange(value: String) {
        _uiState.update { it.copy(editingBreakMinutes = value, settingsError = null) }
    }

    fun saveSettings() {
        val workMin = _uiState.value.editingWorkMinutes.toIntOrNull()
        val breakMin = _uiState.value.editingBreakMinutes.toIntOrNull()

        if (workMin == null || breakMin == null) {
            _uiState.update { it.copy(settingsError = "Vui lòng nhập số hợp lệ") }
            return
        }

        if (workMin !in 1..120) {
            _uiState.update { it.copy(settingsError = "Thời gian tập trung: 1 - 120 phút") }
            return
        }

        if (breakMin !in 1..60) {
            _uiState.update { it.copy(settingsError = "Thời gian nghỉ: 1 - 60 phút") }
            return
        }

        _uiState.update { it.copy(isSettingsVisible = false) }

        viewModelScope.launch {
            repository?.let { repo ->
                val currentSettings = repo.getUserSettings()
                repo.saveUserSettings(
                    currentSettings.copy(
                        workMinutes = workMin,
                        breakMinutes = breakMin
                    )
                )
            }
        }
    }

    fun resetSettingsToDefault() {
        _uiState.update {
            it.copy(
                editingWorkMinutes = "25",
                editingBreakMinutes = "5",
                settingsError = null
            )
        }
    }
}