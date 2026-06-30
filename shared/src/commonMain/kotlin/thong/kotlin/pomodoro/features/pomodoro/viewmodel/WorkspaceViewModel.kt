package thong.kotlin.pomodoro.features.pomodoro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import thong.kotlin.pomodoro.core.config.AppConfig
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.background.model.BackgroundConfig
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningGroupConfig
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro._base.domain.CompactSection
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2
import thong.kotlin.pomodoro.features.pomodoro.ambient.data.AmbientSoundRepository
import thong.kotlin.pomodoro.features.pomodoro.ambient.domain.AmbientSound
import thong.kotlin.pomodoro.features.pomodoro.music.data.MusicRepository
import thong.kotlin.pomodoro.features.pomodoro.music.domain.MusicTrack
import thong.kotlin.pomodoro.features.session.data.LearningSessionManager
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEvent
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEventType
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import thong.kotlin.pomodoro.features.settings.data.BackgroundRepository
import thong.kotlin.pomodoro.features.settings.domain.AppBackground
import kotlin.time.Clock

data class WorkspaceUiState(
    val currentMode: PomodoroMode = PomodoroMode.WORK,
    val learningStyle: LearningStyle = LearningStyle.SOLO,
    val learningGroupConfig: LearningGroupConfig? = null,
    val currentSession: LearningSessionRecord,
    val isChatExpanded: Boolean = false,

    // Background
    val selectedBackgroundId: String = BackgroundRepository.DEFAULT_BACKGROUND_ID,
    val availableBackgrounds: List<AppBackground> = BackgroundRepository.availableBackgrounds,
    val backgroundConfig: BackgroundConfig? = null,

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
    val settingsError: String = "",

    // Exit Modal
    val isExitModalVisible: Boolean = false
)

class WorkspaceViewModel(
    private val soundManager: SoundManager? = DependencyRegistry.soundManager,
    private val repository: UserAppStateRepositoryV2 = DependencyRegistry.userAppStateRepositoryV2,
    private val learningSessionManager: LearningSessionManager = DependencyRegistry.learningSessionManager,
    private val currentSession: LearningSessionRecord
) : ViewModel() {

    // Chỉ có ViewModel mới có quyền lấy ra và gán giá trị mới (sửa state).
    private val _uiState = MutableStateFlow(WorkspaceUiState(currentSession = currentSession))

    // UI (Màn hình) chỉ được phép đọc biến này.
    val uiState: StateFlow<WorkspaceUiState> = _uiState.asStateFlow()

    init {
        loadWorkspaceSettings()
    }

    // UI muốn đổi state thì phải gọi qua hàm của ViewModel
    private fun loadWorkspaceSettings() {
        viewModelScope.launch {
            // Load danh sách dữ liệu tĩnh từ 3 Object Repositories
            _uiState.update { state ->
                state.copy(
                    availableTracks = MusicRepository.availableTracks,
                    availableAmbientSounds = AmbientSoundRepository.availableSounds,
                    availableBackgrounds = BackgroundRepository.availableBackgrounds
                )
            }

            // Lắng nghe dữ liệu cấu hình đã lưu (Database/DataStore)
            repository.getSettingsFlow().collect { settings ->
                _uiState.update { state ->
                    state.copy(
                        selectedBackgroundId = settings.personalSelectedBackgroundId
                            ?: BackgroundRepository.DEFAULT_BACKGROUND_ID,
                        isNotificationEnabled = settings.isNotificationEnabled,
                        selectedTrackId = settings.personalLastSelectedMusicId
                            ?: MusicRepository.DEFAULT_TRACK_ID
                    )
                }
            }
        }
    }

    // --- MUSIC & AUDIO ---
    fun toggleMusic() {
        if (soundManager == null) return

        _uiState.update { state ->
            val newIsPlaying = !state.isMusicPlaying
            if (newIsPlaying) {
                // Sử dụng default track nếu chưa có track nào được chọn
                val trackToPlay = state.selectedTrackId ?: MusicRepository.DEFAULT_TRACK_ID
                soundManager.playBackgroundMusic(trackToPlay)
            } else {
                soundManager.pauseBackgroundMusic()
            }
            state.copy(
                isMusicPlaying = newIsPlaying,
                musicPosition = soundManager.getCurrentPosition()
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
            repository.let { repo ->
                repo.saveUserSettings(
                    repo.getUserSettings().copy(personalSelectedBackgroundId = backgroundId)
                )
            }
        }
    }

    fun updateBackgroundConfig(config: BackgroundConfig) {
        _uiState.update { it.copy(backgroundConfig = config) }
    }

    // --- UI MODES ---
    fun toggleCompactMode() {
        _uiState.update { state ->
            val newValue = !state.isCompactMode
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
                repository.let { repo ->
                    repo.saveUserSettings(
                        repo.getUserSettings().copy(isNotificationEnabled = newValue)
                    )
                }
            }
            state.copy(isNotificationEnabled = newValue)
        }
    }

    // --- LEARNING STYLE
    fun setLearningStyle(style: LearningStyle) {
        _uiState.update { it.copy(learningStyle = style) }
    }

    fun setLearningGroupConfig(config: LearningGroupConfig?) {
        _uiState.update { it.copy(learningGroupConfig = config) }
    }

    // --- SETTINGS FORM ---
    fun toggleSettings() {
        val currentlyVisible = _uiState.value.isSettingsVisible
        if (!currentlyVisible) {
            viewModelScope.launch {
                val currentSettings = repository.getUserSettings()
                _uiState.update {
                    it.copy(
                        isSettingsVisible = true,
                        editingWorkMinutes = currentSettings.personalWorkMinutes.toString(),
                        editingBreakMinutes = currentSettings.personalBreakMinutes.toString(),
                        settingsError = ""
                    )
                }
            }
        } else {
            _uiState.update { it.copy(isSettingsVisible = false) }
        }
    }

    fun onWorkMinutesChange(value: String) {
        _uiState.update { it.copy(editingWorkMinutes = value, settingsError = "") }
    }

    fun onBreakMinutesChange(value: String) {
        _uiState.update { it.copy(editingBreakMinutes = value, settingsError = "") }
    }

    fun saveSettings(onSettingsSaved: ((Int, Int) -> Unit)? = null) {
        val state = _uiState.value
        val currentSession = state.currentSession

        val workMin = state.editingWorkMinutes.trim().toIntOrNull()
        val breakMin = state.editingBreakMinutes.trim().toIntOrNull()

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

        val updatedSession = currentSession.copy(
            plannedWorkMinutes = workMin,
            plannedBreakMinutes = breakMin,
        )

        _uiState.update {
            it.copy(
                isSettingsVisible = false,
                settingsError = "",
                editingWorkMinutes = workMin.toString(),
                editingBreakMinutes = breakMin.toString(),
                currentSession = updatedSession
            )
        }

        viewModelScope.launch {
            try {
                val currentSettings = repository.getUserSettings()

                repository.saveUserSettings(
                    currentSettings.copy(
                        personalWorkMinutes = workMin,
                        personalBreakMinutes = breakMin
                    )
                )
                updateSession(updatedSession)
                insertEvent(
                    LearningSessionEvent(
                        sessionId = updatedSession.sessionId,
                        eventType = LearningSessionEventType.UPDATED_SESSION_SETTINGS,
                        metadata = mapOf(
                            "source" to "settings_ui",
                            "action" to "save_settings",
                            "old_work_minutes" to currentSession.plannedWorkMinutes.toString(),
                            "old_break_minutes" to currentSession.plannedBreakMinutes.toString(),
                            "new_work_minutes" to workMin.toString(),
                            "new_break_minutes" to breakMin.toString()
                        )
                    )
                )
                onSettingsSaved?.invoke(workMin, breakMin)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        settingsError = "Không thể lưu cài đặt. Vui lòng thử lại: ${e.message}",
                        isSettingsVisible = true,
                        currentSession = currentSession
                    )
                }
            }
        }
    }

    fun resetSettingsToDefault() {
        _uiState.update {
            it.copy(
                editingWorkMinutes = AppConfig.DEFAULT_WORK_MINUTES.toString(),
                editingBreakMinutes = AppConfig.DEFAULT_BREAK_MINUTES.toString(),
                settingsError = ""
            )
        }
    }

    // --- EXIT ACTIONS ---
    fun toggleExitModal() {
        _uiState.update { it.copy(isExitModalVisible = !it.isExitModalVisible) }
    }

    fun endSession(onComplete: () -> Unit) {
        val session = _uiState.value.currentSession
        val now = Clock.System.now().toEpochMilliseconds()
        val updatedSession = session.copy(
            status = LearningSessionStatus.COMPLETED,
            endedAtMillis = now,
            updatedAtMillis = now
        )
        _uiState.update { it.copy(currentSession = updatedSession) }
        viewModelScope.launch {
            try {
                updateSession(updatedSession)
                insertEvent(
                    LearningSessionEvent(
                        sessionId = updatedSession.sessionId,
                        eventType = LearningSessionEventType.SESSION_COMPLETED_BY_USER,
                        metadata = mapOf(
                            "source" to "session_ui",
                            "action" to "session_completed_by_user",
                        )
                    )
                )
                val userSettings = repository.getUserSettings()
                repository.saveUserSettings(
                    userSettings.copy(currentSessionId = null)
                )
                onComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        currentSession = session,
                        settingsError = "Không thể kết thúc phiên học. Vui lòng thử lại: ${e.message}"
                    )
                }
            }
        }
    }

    fun pauseSession(onComplete: () -> Unit) {
        val session = _uiState.value.currentSession
        val now = Clock.System.now().toEpochMilliseconds()

        val updatedSession = session.copy(
            status = LearningSessionStatus.PAUSED,
            lastPausedAtMillis = now,
            updatedAtMillis = now
        )

        _uiState.update {
            it.copy(currentSession = updatedSession)
        }

        viewModelScope.launch {
            try {
                updateSession(updatedSession)
                val userSettings = repository.getUserSettings()
                repository.saveUserSettings(
                    userSettings.copy(currentSessionId = updatedSession.sessionId)
                )
                insertEvent(
                    LearningSessionEvent(
                        sessionId = updatedSession.sessionId,
                        eventType = LearningSessionEventType.SESSION_PAUSED_BY_USER,
                        metadata = mapOf(
                            "source" to "session_ui",
                            "action" to "session_paused_by_user",
                        )
                    )
                )
                onComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        currentSession = session,
                        settingsError = "Không thể tạm dừng phiên học. Vui lòng thử lại: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteSession(onComplete: () -> Unit) {
        val session = _uiState.value.currentSession
        val sessionId = session.sessionId

        viewModelScope.launch {
            try {
                learningSessionManager.deleteSessionById(sessionId)
                val userSettings = repository.getUserSettings()
                repository.saveUserSettings(
                    userSettings.copy(currentSessionId = null)
                )
                insertEvent(
                    LearningSessionEvent(
                        sessionId = sessionId,
                        eventType = LearningSessionEventType.SESSION_DELETED_BY_USER,
                        metadata = mapOf(
                            "source" to "session_ui",
                            "action" to "session_deleted_by_user",
                        )
                    )
                )
                onComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        settingsError = "Không thể xóa phiên học. Vui lòng thử lại: ${e.message}"
                    )
                }
            }
        }
    }

    suspend fun insertEvent(event: LearningSessionEvent) = learningSessionManager.insertEvent(event)

    suspend fun updateSession(session: LearningSessionRecord) = learningSessionManager.updateSession(session)
}