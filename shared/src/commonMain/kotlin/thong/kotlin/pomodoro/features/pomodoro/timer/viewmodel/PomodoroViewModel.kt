package thong.kotlin.pomodoro.features.pomodoro.timer.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import thong.kotlin.pomodoro.core.media.SoundManager
import kotlin.random.Random
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.EventType
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.PomodoroConfig
import thong.kotlin.pomodoro.features.pomodoro.timer.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.Task
import thong.kotlin.pomodoro.features.pomodoro.timer.state.PomodoroUiState
import thong.kotlin.pomodoro.features.pomodoro.timer.state.CompactSection
import thong.kotlin.pomodoro.features.pomodoro.timer.state.totalSeconds

import thong.kotlin.pomodoro.features.pomodoro.music.data.MusicRepository
import thong.kotlin.pomodoro.features.pomodoro.ambient.data.AmbientSoundRepository
import thong.kotlin.pomodoro.features.settings.data.BackgroundRepository
import thong.kotlin.pomodoro.features.pomodoro.domain.repository.UserAppStateRepository
import thong.kotlin.pomodoro.features.pomodoro.domain.model.UserSettings
import thong.kotlin.pomodoro.features.pomodoro.domain.model.SessionRecord
import thong.kotlin.pomodoro.core.utils.getCurrentDateString
import thong.kotlin.pomodoro.core.utils.getCurrentDateTimeString

class PomodoroViewModel(
    private val viewModelScope: CoroutineScope,
    private val soundManager: SoundManager? = null,
    private val repository: UserAppStateRepository? = null,
    initialState: PomodoroUiState = PomodoroUiState()
) {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    init {
        loadSavedState()
    }

    private fun loadSavedState() {
        val savedSettings = repository?.getUserSettings() ?: UserSettings()
        
        _uiState.update { state ->
            state.copy(
                config = state.config.copy(
                    workMinutes = savedSettings.workMinutes,
                    shortBreakMinutes = savedSettings.breakMinutes
                ),
                timeLeft = if (!state.isActive) {
                    state.currentMode.totalSeconds(state.config.copy(
                        workMinutes = savedSettings.workMinutes,
                        shortBreakMinutes = savedSettings.breakMinutes
                    ))
                } else state.timeLeft,
                selectedBackgroundId = savedSettings.selectedBackgroundId ?: state.selectedBackgroundId ?: BackgroundRepository.DEFAULT_BACKGROUND_ID,
                isNotificationEnabled = savedSettings.isNotificationEnabled,
                isCompactMode = savedSettings.isCompactMode,
                availableTracks = MusicRepository.availableTracks,
                selectedTrackId = state.selectedTrackId ?: MusicRepository.DEFAULT_TRACK_ID,
                availableAmbientSounds = AmbientSoundRepository.availableSounds,
                availableBackgrounds = BackgroundRepository.availableBackgrounds
            )
        }

        // Load tasks and today's stats from repository
        viewModelScope.launch {
            repository?.getAllTasks()?.collect { tasks ->
                _uiState.update { it.copy(tasks = tasks) }
            }
        }

        viewModelScope.launch {
            repository?.getTodayStats()?.collect { stats ->
                if (stats != null) {
                    _uiState.update { it.copy(pomodorosToday = stats.sessionsCompleted) }
                }
            }
        }

        // Reactively update settings (like learning style)
        viewModelScope.launch {
            repository?.getSettingsFlow()?.collect { settings ->
                _uiState.update { state ->
                    state.copy(
                        learningStyle = settings.learningStyle,
                        config = state.config.copy(
                            workMinutes = settings.workMinutes,
                            shortBreakMinutes = settings.breakMinutes
                        )
                    )
                }
            }
        }
    }

    // Quản lý Job đếm ngược của Coroutines để có thể hủy (Cancel) bất cứ lúc nào
    private var timerJob: Job? = null

    fun updateConfig(config: PomodoroConfig) {
        timerJob?.cancel()

        _uiState.update {
            it.copy(
                config = config,
                isActive = false,
                timeLeft = it.currentMode.totalSeconds(config),
                event = EventType.NOTHING
            )
        }
    }

    /**
     * Nút bấm chính: Chuyển đổi qua lại giữa Bắt đầu (Play) và Tạm dừng (Pause)
     */
    fun toggleTimer() {
        if (_uiState.value.isActive) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _uiState.update { currentState ->
            currentState.copy(
                isActive = true,
                event = resolveStartEvent(currentState)
            )
        }

        // Hủy job cũ đề phòng trường hợp bị kích hoạt trùng lặp
        timerJob?.cancel()

        // Kích hoạt vòng lặp đếm ngược bất đồng bộ
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1000) // Trì hoãn chính xác 1 giây
                _uiState.update { state ->
                    val newTimeLeft = state.timeLeft - 1
                    
                    // Xử lý âm thanh thông báo giây cuối
                    if (newTimeLeft in 1L..4L) {
                        soundManager?.playBeepSound()
                    }
                    
                    state.copy(timeLeft = newTimeLeft)
                }
            }
            // Khi timeLeft về bằng 0, tự động chạy logic hoàn thành
            handleTimerComplete()
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { currentState ->
            currentState.copy(
                isActive = false,
                event = resolvePauseEvent(currentState)
            )
        }
    }

    /**
     * Nút Đặt lại (Reset): Đưa thời gian về mốc ban đầu của Chế độ hiện tại
     */
    fun resetTimer() {
        pauseTimer()
        _uiState.update {
            it.copy(
                isActive = false,
                timeLeft = it.currentMode.totalSeconds(it.config),
                event = EventType.NOTHING
            )
        }
    }

    /**
     * Nút Bỏ qua (Skip): Ép kết thúc phiên hiện tại ngay lập tức
     */
    fun skipTimer() {
        timerJob?.cancel()
        val currentState = _uiState.value

        if (currentState.currentMode == PomodoroMode.WORK) {
            // Skip khi chưa học xong thì KHÔNG cộng pomodoro
            _uiState.update {
                it.copy(
                    isActive = false,
                    currentMode = PomodoroMode.SHORT_BREAK,
                    timeLeft = PomodoroMode.SHORT_BREAK.totalSeconds(it.config),
                    event = EventType.NOTHING
                )
            }
        } else {
            // Skip khi đang nghỉ thì quay lại phiên WORK mới
            _uiState.update {
                it.copy(
                    isActive = false,
                    currentMode = PomodoroMode.WORK,
                    timeLeft = PomodoroMode.WORK.totalSeconds(it.config),
                    event = EventType.NOTHING
                )
            }
        }
    }

//    /**
//     * Người dùng tự chọn thủ công Chế độ học ở thanh chọn dưới đáy
//     */
//    fun switchMode(mode: PomodoroMode) {
//        timerJob?.cancel()
//        _uiState.update {
//            it.copy(
//                isActive = false,
//                currentMode = mode,
//                timeLeft = mode.totalSeconds(it.config),
//                event = EventType.NOTHING
//            )
//        }
//    }

    /**
     * Xử lý tự động khi hết giờ: Học xong thì chuyển sang Nghỉ, Nghỉ xong thì quay lại Học
     */
    private fun handleTimerComplete(resetEvent: Boolean = false) {
        soundManager?.playChimeSound()
        pauseTimer()
        val currentState = _uiState.value

        val (newMode, nextTime, eventType, notification) = if (currentState.currentMode == PomodoroMode.WORK) {
            // Nếu vừa học xong: Tăng số Pomo trong ngày và chuyển sang Nghỉ ngắn
            val nextMode = PomodoroMode.SHORT_BREAK
            val nextTime = nextMode.totalSeconds(currentState.config)
            
            // Save Session and Stats
            viewModelScope.launch {
                repository?.saveSession(
                    SessionRecord(
                        id = Random.nextInt().toString(),
                        startTime = getCurrentDateTimeString(),
                        endTime = getCurrentDateTimeString(),
                        mode = PomodoroMode.WORK.name,
                        durationMinutes = currentState.config.workMinutes,
                        status = "COMPLETED",
                        tasksCompletedCount = currentState.tasks.count { it.isCompleted }
                    )
                )
                repository?.incrementDailyStats(
                    sessionsCompleted = 1,
                    focusMinutes = currentState.config.workMinutes,
                    tasksCompleted = currentState.tasks.count { it.isCompleted }
                )
            }
            
            listOf(nextMode, nextTime, EventType.WORK_END, "Work session completed. Time for a break!")
        } else {
            // Nếu vừa nghỉ xong: Quay trở lại chế độ Tập trung
            val nextMode = PomodoroMode.WORK
            val nextTime = nextMode.totalSeconds(currentState.config)
            
            viewModelScope.launch {
                repository?.incrementDailyStats(
                    breakMinutes = currentState.config.shortBreakMinutes
                )
            }

            listOf(nextMode, nextTime, EventType.BREAK_END, "Break finished. Time to focus again!")
        }

        _uiState.update { state ->
            state.copy(
                pomodorosToday = if (currentState.currentMode == PomodoroMode.WORK) state.pomodorosToday + 1 else state.pomodorosToday,
                currentMode = newMode as PomodoroMode,
                timeLeft = nextTime as Long,
                event = eventType as EventType,
                pendingNotification = notification as String
            )
        }
    }

    fun onNewTaskTextChange(text: String) {
        _uiState.update { it.copy(newTaskText = text) }
    }

    fun addTask() {
        val text = _uiState.value.newTaskText
        if (text.isNotBlank()) {
            val taskId = Random.nextInt().toString()
            val newTask = Task(
                id = taskId,
                text = text,
                createdAt = getCurrentDateTimeString()
            )
            
            _uiState.update {
                it.copy(
                    tasks = it.tasks + newTask,
                    newTaskText = ""
                )
            }
            
            viewModelScope.launch {
                repository?.saveTask(newTask)
            }
        }
    }

    fun deleteTask(taskId: String) {
        _uiState.update { state ->
            state.copy(tasks = state.tasks.filter { it.id != taskId })
        }
        viewModelScope.launch {
            repository?.deleteTask(taskId)
        }
    }

    fun toggleTask(taskId: String) {
        var updatedTask: Task? = null
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.map {
                    if (it.id == taskId) {
                        val newStatus = !it.isCompleted
                        updatedTask = it.copy(
                            isCompleted = newStatus,
                            completedAt = if (newStatus) getCurrentDateTimeString() else null
                        )
                        updatedTask!!
                    } else it
                }
            )
        }
        updatedTask?.let { task ->
            viewModelScope.launch {
                repository?.saveTask(task)
            }
        }
    }

    fun toggleMusic() {
        _uiState.update { state ->
            val newIsPlaying = !state.isMusicPlaying
            if (newIsPlaying) {
                state.selectedTrackId?.let { trackId ->
                    soundManager?.playBackgroundMusic(trackId)
                }
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
                musicPosition = 0L // Reset position when changing track
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

    fun selectBackground(backgroundId: String) {
        _uiState.update { it.copy(selectedBackgroundId = backgroundId) }
        repository?.let { repo ->
            repo.saveUserSettings(repo.getUserSettings().copy(selectedBackgroundId = backgroundId))
        }
    }

    fun toggleTasksExpanded() {
        _uiState.update { it.copy(isTasksExpanded = !it.isTasksExpanded) }
    }

    fun toggleSettings() {
        _uiState.update { state ->
            if (!state.isSettingsVisible) {
                // Pre-fill editing values when opening
                state.copy(
                    isSettingsVisible = true,
                    editingWorkMinutes = state.config.workMinutes.toString(),
                    editingBreakMinutes = state.config.shortBreakMinutes.toString(),
                    settingsError = null
                )
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

        val newConfig = _uiState.value.config.copy(
            workMinutes = workMin,
            shortBreakMinutes = breakMin
        )

        updateConfig(newConfig)
        _uiState.update { it.copy(isSettingsVisible = false) }

        // Save to repository
        repository?.let { repo ->
            val currentSettings = repo.getUserSettings()
            repo.saveUserSettings(currentSettings.copy(
                workMinutes = workMin,
                breakMinutes = breakMin
            ))
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

    fun hardResetData() {
        viewModelScope.launch {
            repository?.clearAllData()
            // In a real app, we might want to trigger a restart or navigate to Splash.
            // For now, we'll at least reset the current UI state to defaults.
            _uiState.update { 
                PomodoroUiState().copy(
                    availableTracks = MusicRepository.availableTracks,
                    availableAmbientSounds = AmbientSoundRepository.availableSounds,
                    availableBackgrounds = BackgroundRepository.availableBackgrounds
                )
            }
        }
    }

    fun updateBackgroundConfig(config: thong.kotlin.pomodoro.features.background.model.BackgroundConfig) {
        _uiState.update { it.copy(backgroundConfig = config) }
    }

    fun toggleCompactMode() {
        _uiState.update { state ->
            val newValue = !state.isCompactMode
            repository?.let { repo ->
                repo.saveUserSettings(repo.getUserSettings().copy(isCompactMode = newValue))
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
            repository?.let { repo ->
                repo.saveUserSettings(repo.getUserSettings().copy(isNotificationEnabled = newValue))
            }
            state.copy(isNotificationEnabled = newValue)
        }
    }

    fun clearPendingNotification() {
        _uiState.update { it.copy(pendingNotification = null) }
    }

    private fun resumeTimerAfterRestore() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0 && _uiState.value.isActive) {
                delay(1000)
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }

            if (_uiState.value.timeLeft <= 0) {
                handleTimerComplete()
            }
        }
    }

    private fun resolveStartEvent(state: PomodoroUiState): EventType {
        return when (state.event) {
            EventType.NOTHING,
            EventType.CLICK_PAUSE_WORK -> EventType.CLICK_START_WORK
            EventType.CLICK_PAUSE_BREAK -> EventType.CLICK_START_BREAK
            EventType.CLICK_START_WORK,
            EventType.BREAK_END,
            EventType.WORK_END,
            EventType.CLICK_START_BREAK -> state.event
        }
    }

    private fun resolvePauseEvent(state: PomodoroUiState): EventType {
        return when (state.event) {
            EventType.CLICK_START_WORK -> EventType.CLICK_PAUSE_WORK
            EventType.CLICK_START_BREAK -> EventType.CLICK_PAUSE_BREAK
            EventType.NOTHING,
            EventType.CLICK_PAUSE_WORK,
            EventType.BREAK_END,
            EventType.WORK_END,
            EventType.CLICK_PAUSE_BREAK -> state.event
        }
    }
}