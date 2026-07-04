package thong.kotlin.pomodoro.features.pomodoro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import thong.kotlin.pomodoro.core.config.AppConfig
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.core.notification.NotificationManager
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.background.model.BackgroundConfig
import thong.kotlin.pomodoro.features.focus.score.domain.FocusScoreCalculator
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningGroupConfig
import thong.kotlin.pomodoro.features.learning.mode.domain.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro._base.domain.CompactSection
import thong.kotlin.pomodoro.features.pomodoro._base.domain.EventType
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroConfig
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.totalSeconds
import thong.kotlin.pomodoro.features.pomodoro.ambient.data.AmbientSoundRepository
import thong.kotlin.pomodoro.features.pomodoro.music.data.MusicRepository
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.SessionTask
import thong.kotlin.pomodoro.features.session.data.LearningSessionManager
import thong.kotlin.pomodoro.features.session.domain.CurrentLearningMode
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEvent
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEventType
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import thong.kotlin.pomodoro.features.background.data.BackgroundRepository
import kotlin.time.Clock

data class TotallyPomodoroUiState(
    val currentSession: LearningSessionRecord,
    val currentMode: PomodoroMode = PomodoroMode.WORK,
    val timerUiState: TimerUiState,
    val workspaceUiState: WorkspaceUiState,
    val tasksUiState: TasksUiState,
)

class AppViewModel(
    private val soundManager: SoundManager? = DependencyRegistry.soundManager,
    private val notificationManager: NotificationManager? = DependencyRegistry.notificationManager,
    private val repository: UserAppStateRepositoryV2 = DependencyRegistry.userAppStateRepositoryV2,
    private val learningSessionManager: LearningSessionManager = DependencyRegistry.learningSessionManager,
    private val currentSession: LearningSessionRecord,
    private val isNewSession: Boolean,
) : ViewModel() {

    val timerUiState = TimerUiState(
        timeLeft = currentSession.plannedWorkMinutes * 60,
        config = PomodoroConfig(
            workMinutes = currentSession.plannedWorkMinutes,
            shortBreakMinutes = currentSession.plannedBreakMinutes
        )
    )
    val workspaceUiState = WorkspaceUiState(currentSession = currentSession)
    val taskUiState = TasksUiState()

    private val _uiState = MutableStateFlow(
        TotallyPomodoroUiState(
            currentSession = currentSession,
            timerUiState = timerUiState,
            workspaceUiState = workspaceUiState,
            tasksUiState = taskUiState
        )
    )
    val uiState: StateFlow<TotallyPomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadInitialTimerStateData()
        loadWorkspaceSettings()
        loadTasks()
    }

    private fun loadInitialTimerStateData() {
        _uiState.update { state ->
            val mode = when (currentSession.currentLearningMode) {
                CurrentLearningMode.WORK -> PomodoroMode.WORK
                CurrentLearningMode.BREAK -> PomodoroMode.SHORT_BREAK
                CurrentLearningMode.LONG_BREAK -> PomodoroMode.LONG_BREAK
                CurrentLearningMode.NOT_YET_STARTED -> PomodoroMode.WORK
            }

            val config = PomodoroConfig(
                workMinutes = currentSession.plannedWorkMinutes,
                shortBreakMinutes = currentSession.plannedBreakMinutes,
                longBreakMinutes = currentSession.plannedLongBreakMinutes
            )

            val timerState = state.timerUiState
            val workspaceState = state.workspaceUiState

            state.copy(
                currentMode = mode,
                timerUiState = timerState.copy(
                    config = config,
                    timeLeft = mode.totalSeconds(config),
                    pomodorosToday = currentSession.completedWorkRounds,
                    isSessionStarted = currentSession.status != LearningSessionStatus.IDLE,
                    isActive = currentSession.status == LearningSessionStatus.RUNNING
                ),
                workspaceUiState = workspaceState.copy(
                    currentMode = mode,
                    learningStyle = currentSession.sessionMode,
                    editingWorkMinutes = currentSession.plannedWorkMinutes.toString(),
                    editingBreakMinutes = currentSession.plannedBreakMinutes.toString(),

                )
            )
        }

        // Nếu phiên đang chạy thì bắt đầu đếm ngược ngay lập tức
        if (_uiState.value.timerUiState.isActive) {
            startTimer()
        }
    }

    private fun loadWorkspaceSettings() {
        viewModelScope.launch {
            if (isNewSession) {
                repository.getSettingsFlow().collect { settings ->
                    _uiState.update { state ->
                        state.copy(
                            workspaceUiState = _uiState.value.workspaceUiState.copy(
                                availableTracks = MusicRepository.availableTracks,
                                availableAmbientSounds = AmbientSoundRepository.availableSounds,
                                availableBackgrounds = BackgroundRepository.availableBackgrounds,
                                selectedBackgroundId = settings.personalSelectedBackgroundId,
                                selectedTrackId =  settings.personalLastSelectedMusicId,
                                isNotificationEnabled = settings.isNotificationEnabled
                            )
                        )
                    }
                }
            } else {
                // Load danh sách dữ liệu tĩnh từ 3 Object Repositories
                _uiState.update { state ->
                    state.copy(
                        workspaceUiState = _uiState.value.workspaceUiState.copy(
                            availableTracks = MusicRepository.availableTracks,
                            availableAmbientSounds = AmbientSoundRepository.availableSounds,
                            availableBackgrounds = BackgroundRepository.availableBackgrounds,
                            selectedTrackId = currentSession.lastMusicId,
                            selectedBackgroundId = currentSession.lastBackgroundId ?: BackgroundRepository.DEFAULT_BACKGROUND_ID,
                            activeAmbientSoundIds = currentSession.lastAmbientSounds.toSet(),
                        )
                    )
                }
            }
        }
    }

    private fun loadTasks() {
        val sessionId = _uiState.value.currentSession.sessionId
        viewModelScope.launch {
            val tasks = learningSessionManager.getAllTasksBySessionId(sessionId)

            _uiState.update { state ->
                state.copy(
                    tasksUiState = state.tasksUiState.copy(
                        sessionTasks = tasks
                    )
                )
            }
        }
    }

    fun updateConfig(config: PomodoroConfig) {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                currentSession = it.currentSession.copy(
                    plannedWorkMinutes = config.workMinutes,
                    plannedBreakMinutes = config.shortBreakMinutes
                ),
                timerUiState = it.timerUiState.copy(
                    config = config,
                    isActive = false,
                    timeLeft = it.currentMode.totalSeconds(config),
                    event = EventType.NOTHING
                )
            )
        }
    }

    private fun startTimer() {
        _uiState.update { currentState ->
            currentState.copy(
                timerUiState = currentState.timerUiState.copy(
                    isActive = true,
                    event = resolveStartEvent(currentState.timerUiState)
                )
            )
        }

        timerJob?.cancel() // Hủy job cũ đề phòng trùng lặp

        timerJob = viewModelScope.launch {
            while (_uiState.value.timerUiState.timeLeft > 0) {
                delay(1000) // Đếm lùi 1 giây
                _uiState.update { state ->
                    val newTimeLeft = state.timerUiState.timeLeft - 1

                    // Phát tiếng bíp ở những giây cuối
                    val userSettings = repository.getUserSettings()
                    val shouldPlayChime = userSettings.isSoundEnabled
                    if (shouldPlayChime && newTimeLeft in 1L..4L) {
                        soundManager?.playBeepSound()
                    }

                    state.copy(
                        timerUiState = state.timerUiState.copy(
                            timeLeft = newTimeLeft
                        )
                    )
                }
            }
            handleTimerComplete()
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { currentState ->
            currentState.copy(
                timerUiState = currentState.timerUiState.copy(
                    isActive = false,
                    event = resolvePauseEvent(currentState.timerUiState)
                )
            )
        }
    }

    fun toggleTimer() {
        val state = _uiState.value
        val session = state.currentSession
        val currentMode = state.currentMode
        val totalSeconds = currentMode.totalSeconds(state.timerUiState.config)

        val isStartOfRound = state.timerUiState.timeLeft == totalSeconds
        val isMiddleOfRound = state.timerUiState.timeLeft in 1 until totalSeconds

        val eventType: LearningSessionEventType
        val action: String
        val nextStatus: LearningSessionStatus
        val nextLearningMode: CurrentLearningMode
        val shouldStartTimer: Boolean
        val shouldInsertSessionStartedEvent: Boolean

        when {
            state.timerUiState.isActive && currentMode == PomodoroMode.WORK -> {
                eventType = LearningSessionEventType.WORK_ROUND_PAUSED
                action = "pause_work"
                nextStatus = LearningSessionStatus.PAUSED
                nextLearningMode = CurrentLearningMode.WORK
                shouldStartTimer = false
                shouldInsertSessionStartedEvent = false
            }

            state.timerUiState.isActive && currentMode == PomodoroMode.SHORT_BREAK -> {
                eventType = LearningSessionEventType.BREAK_ROUND_PAUSED
                action = "pause_break"
                nextStatus = LearningSessionStatus.PAUSED
                nextLearningMode = CurrentLearningMode.BREAK
                shouldStartTimer = false
                shouldInsertSessionStartedEvent = false
            }

            !state.timerUiState.isActive && currentMode == PomodoroMode.WORK && isMiddleOfRound -> {
                eventType = LearningSessionEventType.WORK_ROUND_RESUMED
                action = "resume_work"
                nextStatus = LearningSessionStatus.RUNNING
                nextLearningMode = CurrentLearningMode.WORK
                shouldStartTimer = true
                shouldInsertSessionStartedEvent = false
            }

            !state.timerUiState.isActive && currentMode == PomodoroMode.WORK && isStartOfRound -> {
                eventType = LearningSessionEventType.WORK_ROUND_STARTED
                action = "start_work"
                nextStatus = LearningSessionStatus.RUNNING
                nextLearningMode = CurrentLearningMode.WORK
                shouldStartTimer = true
                shouldInsertSessionStartedEvent = !state.timerUiState.isSessionStarted
            }

            !state.timerUiState.isActive && currentMode == PomodoroMode.SHORT_BREAK && isMiddleOfRound -> {
                eventType = LearningSessionEventType.BREAK_ROUND_RESUMED
                action = "resume_break"
                nextStatus = LearningSessionStatus.RUNNING
                nextLearningMode = CurrentLearningMode.BREAK
                shouldStartTimer = true
                shouldInsertSessionStartedEvent = false
            }

            !state.timerUiState.isActive && currentMode == PomodoroMode.SHORT_BREAK && isStartOfRound -> {
                eventType = LearningSessionEventType.BREAK_ROUND_STARTED
                action = "start_break"
                nextStatus = LearningSessionStatus.RUNNING
                nextLearningMode = CurrentLearningMode.BREAK
                shouldStartTimer = true
                shouldInsertSessionStartedEvent = false
            }

            else -> return
        }

        var updatedSession = session.copy(
            status = nextStatus,
            currentLearningMode = nextLearningMode,
            completedWorkRounds = state.timerUiState.pomodorosToday,
            pausedCount = if (eventType == LearningSessionEventType.WORK_ROUND_PAUSED || eventType == LearningSessionEventType.BREAK_ROUND_PAUSED) {
                session.pausedCount + 1
            } else session.pausedCount
        )

        if (shouldStartTimer) {
            _uiState.update {
                it.copy(
                    currentSession = updatedSession,
                    timerUiState = it.timerUiState.copy(
                        isActive = true,
                        isSessionStarted = it.timerUiState.isSessionStarted || shouldInsertSessionStartedEvent,
                        isJustEndedBreak = false
                    )
                )
            }

            startTimer()
        } else {
            stopTimerJobOnly(timerJob)

            _uiState.update {
                it.copy(
                    currentSession = updatedSession,
                    timerUiState = it.timerUiState.copy(
                        isActive = false
                    )
                )
            }
        }

        viewModelScope.launch {
            if (shouldInsertSessionStartedEvent) {
                updatedSession = updatedSession.copy(
                    startedAtMillis = Clock.System.now().toEpochMilliseconds()
                )
                insertEvent(
                    LearningSessionEvent(
                        sessionId = session.sessionId,
                        eventType = LearningSessionEventType.SESSION_STARTED,
                        remainingSeconds = state.timerUiState.timeLeft,
                        metadata = mapOf(
                            "source" to "toggle_timer",
                            "action" to "start_session"
                        )
                    )
                )
            }
            updateSession(updatedSession)

            insertEvent(
                LearningSessionEvent(
                    sessionId = session.sessionId,
                    eventType = eventType,
                    remainingSeconds = state.timerUiState.timeLeft,
                    currentRound = state.timerUiState.pomodorosToday,
                    metadata = mapOf(
                        "source" to "toggle_timer",
                        "action" to action,
                        "mode" to currentMode.name
                    )
                )
            )
        }
    }

    fun resetTimer() {
        pauseTimer()
        val state = _uiState.value
        val currentSession = state.currentSession
        val totalSeconds = state.currentMode.totalSeconds(state.timerUiState.config)
        val hasStarted = state.timerUiState.timeLeft < totalSeconds
        stopTimerJobOnly(timerJob)

        val updatedSession = currentSession.copy(
            status = LearningSessionStatus.PAUSED
        )

        _uiState.update {
            it.copy(
                currentSession = updatedSession,
                timerUiState = it.timerUiState.copy(
                    isActive = false,
                    timeLeft = totalSeconds,
                    event = EventType.NOTHING
                )
            )
        }

        if (!hasStarted) return

        viewModelScope.launch {
            val eventType = when (state.currentMode) {
                PomodoroMode.WORK -> LearningSessionEventType.WORK_ROUND_RESET
                PomodoroMode.SHORT_BREAK -> LearningSessionEventType.BREAK_ROUND_RESET
                PomodoroMode.LONG_BREAK -> LearningSessionEventType.BREAK_ROUND_RESET
            }

            val action = when (state.currentMode) {
                PomodoroMode.WORK -> "reset_work"
                PomodoroMode.SHORT_BREAK -> "reset_break"
                PomodoroMode.LONG_BREAK -> "reset_long_break"
            }

            insertEvent(
                LearningSessionEvent(
                    sessionId = currentSession.sessionId,
                    eventType = eventType,
                    remainingSeconds = state.timerUiState.timeLeft,
                    currentRound = state.timerUiState.pomodorosToday,
                    metadata = mapOf(
                        "source" to "reset_timer",
                        "action" to action,
                        "mode" to state.currentMode.name
                    )
                )
            )

            updateSession(updatedSession)
        }
    }

    fun skipTimer() {
        stopTimerJobOnly(timerJob)
        val state = _uiState.value
        val currentSession = state.currentSession
        val currentMode = state.currentMode
        val currentTotalSeconds = currentMode.totalSeconds(state.timerUiState.config)

        val elapsedSeconds = currentTotalSeconds - state.timerUiState.timeLeft

        val nextMode = when (currentMode) {
            PomodoroMode.WORK -> PomodoroMode.SHORT_BREAK
            PomodoroMode.SHORT_BREAK -> PomodoroMode.WORK
            PomodoroMode.LONG_BREAK -> PomodoroMode.WORK
        }

        val nextLearningMode = when (nextMode) {
            PomodoroMode.WORK -> CurrentLearningMode.WORK
            PomodoroMode.SHORT_BREAK -> CurrentLearningMode.BREAK
            PomodoroMode.LONG_BREAK -> CurrentLearningMode.LONG_BREAK
        }

        val updatedSession = when (currentMode) {
            PomodoroMode.WORK -> {
                currentSession.copy(
                    status = LearningSessionStatus.PAUSED,
                    currentLearningMode = nextLearningMode,
                    totalFocusSeconds = currentSession.totalFocusSeconds + elapsedSeconds,
                    skipCount = currentSession.skipCount + 1
                )
            }

            PomodoroMode.SHORT_BREAK,
            PomodoroMode.LONG_BREAK -> {
                currentSession.copy(
                    status = LearningSessionStatus.PAUSED,
                    currentLearningMode = nextLearningMode,
                    totalBreakSeconds = currentSession.totalBreakSeconds + elapsedSeconds,
                    skipCount = currentSession.skipCount + 1
                )
            }
        }

        _uiState.update {
            it.copy(
                currentMode = nextMode,
                currentSession = updatedSession,
                timerUiState = it.timerUiState.copy(
                    isActive = false,
                    timeLeft = nextMode.totalSeconds(it.timerUiState.config),
                    event = EventType.NOTHING
                )
            )
        }

        viewModelScope.launch {
            val eventType = when (currentMode) {
                PomodoroMode.WORK -> LearningSessionEventType.WORK_ROUND_SKIPPED
                PomodoroMode.SHORT_BREAK,
                PomodoroMode.LONG_BREAK -> LearningSessionEventType.BREAK_ROUND_SKIPPED
            }

            val action = when (currentMode) {
                PomodoroMode.WORK -> "skip_work"
                PomodoroMode.SHORT_BREAK -> "skip_break"
                PomodoroMode.LONG_BREAK -> "skip_long_break"
            }

            updateSession(updatedSession)

            insertEvent(
                LearningSessionEvent(
                    sessionId = currentSession.sessionId,
                    eventType = eventType,
                    remainingSeconds = state.timerUiState.timeLeft,
                    currentRound = state.timerUiState.pomodorosToday,
                    metadata = mapOf(
                        "source" to "toggle_skip_timer",
                        "action" to action,
                        "from_mode" to currentMode.name,
                        "to_mode" to nextMode.name,
                        "elapsed_seconds" to elapsedSeconds.toString()
                    )
                )
            )
        }
    }

    private fun handleTimerComplete() {
        viewModelScope.launch {
            val userSettings = repository.getUserSettings()

            if (userSettings.isSoundEnabled) {
                soundManager?.playChimeSound()
            }

            stopTimerJobOnly(timerJob)

            val state = _uiState.value
            val session = state.currentSession
            val currentMode = state.currentMode
            val config = state.timerUiState.config

            val isWorkMode = currentMode == PomodoroMode.WORK

            val nextMode = if (isWorkMode) {
                PomodoroMode.SHORT_BREAK
            } else {
                PomodoroMode.WORK
            }

            val nextTime = nextMode.totalSeconds(config)

            val updatedSession = if (isWorkMode) {
                session.copy(
                    status = LearningSessionStatus.PAUSED,
                    currentLearningMode = CurrentLearningMode.BREAK,
                    completedWorkRounds = session.completedWorkRounds + 1,
                    totalFocusSeconds = session.totalFocusSeconds + currentMode.totalSeconds(config)
                )
            } else {
                session.copy(
                    status = LearningSessionStatus.PAUSED,
                    currentLearningMode = CurrentLearningMode.WORK,
                    completedBreakRounds = session.completedBreakRounds + 1,
                    totalBreakSeconds = session.totalBreakSeconds + currentMode.totalSeconds(config)
                )
            }

            val learningEventType = if (isWorkMode) {
                LearningSessionEventType.WORK_ROUND_COMPLETED
            } else {
                LearningSessionEventType.BREAK_ROUND_ENDED
            }

            val timerEventType = if (isWorkMode) {
                EventType.WORK_END
            } else {
                EventType.BREAK_END
            }

            val notificationMessage = if (isWorkMode) {
                "Work session completed. Time for a break!"
            } else {
                "Break finished. Time to focus again!"
            }

            val currentRound = if (isWorkMode) {
                updatedSession.completedWorkRounds
            } else {
                updatedSession.completedBreakRounds
            }

            showTimerCompletedNotification()

            _uiState.update { currentState ->
                currentState.copy(
                    currentMode = nextMode,
                    currentSession = updatedSession,
                    timerUiState = currentState.timerUiState.copy(
                        isActive = false,
                        pomodorosToday = if (isWorkMode) {
                            currentState.timerUiState.pomodorosToday + 1
                        } else {
                            currentState.timerUiState.pomodorosToday
                        },
                        isJustEndedBreak = !isWorkMode,
                        timeLeft = nextTime,
                        event = timerEventType,
                        pendingNotification = notificationMessage
                    )
                )
            }

            updateSession(updatedSession)

            insertEvent(
                LearningSessionEvent(
                    eventId = "auto_event_${Clock.System.now().toEpochMilliseconds()}",
                    sessionId = session.sessionId,
                    eventType = learningEventType,
                    remainingSeconds = 0,
                    currentRound = currentRound,
                    metadata = mapOf(
                        "source" to "auto",
                        "action" to if (isWorkMode) {
                            "work_round_complete"
                        } else {
                            "break_ended"
                        },
                        "from_mode" to currentMode.name,
                        "to_mode" to nextMode.name
                    )
                )
            )

            delay(2000)

            if (userSettings.autoStartWork && currentMode == PomodoroMode.SHORT_BREAK) {
                toggleTimer()
            } else if (userSettings.autoStartBreak && currentMode == PomodoroMode.WORK) {
                toggleTimer()
            }
        }
    }

    private fun resolveStartEvent(state: TimerUiState): EventType {
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

    private fun resolvePauseEvent(state: TimerUiState): EventType {
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

    fun toggleMusic() {
        if (soundManager == null) return

        val currentState = _uiState.value
        val currentWorkspace = currentState.workspaceUiState
        val currentSession = currentState.currentSession

        val newIsPlaying = !currentWorkspace.isMusicPlaying
        val trackToPlay = currentWorkspace.selectedTrackId ?: MusicRepository.DEFAULT_TRACK_ID

        if (newIsPlaying) {
            soundManager.playBackgroundMusic(trackToPlay)
        } else {
            soundManager.pauseBackgroundMusic()
        }

        val updatedSession = if (newIsPlaying) {
            currentSession.copy(lastMusicId = trackToPlay)
        } else {
            currentSession
        }

        _uiState.update { state ->
            state.copy(
                workspaceUiState = state.workspaceUiState.copy(
                    isMusicPlaying = newIsPlaying,
                    selectedTrackId = trackToPlay,
                    musicPosition = soundManager.getCurrentPosition()
                ),
                currentSession = updatedSession
            )
        }

        viewModelScope.launch {
            updateSession(updatedSession)

            insertEvent(
                LearningSessionEvent(
                    sessionId = updatedSession.sessionId,
                    eventType = LearningSessionEventType.MUSIC_CHANGED,
                    remainingSeconds = _uiState.value.timerUiState.timeLeft,
                    metadata = mapOf(
                        "source" to "workspace",
                        "action" to if (newIsPlaying) "music_play" else "music_pause",
                        "track_id" to trackToPlay
                    )
                )
            )
        }
    }

    fun selectTrack(trackId: String) {
        val currentState = _uiState.value
        val currentWorkspace = currentState.workspaceUiState
        val currentSession = currentState.currentSession
        val isMusicPlaying = currentWorkspace.isMusicPlaying

        val updatedSession = if (isMusicPlaying) {
            currentSession.copy(lastMusicId = trackId)
        } else {
            currentSession
        }

        _uiState.update { state ->
            if (isMusicPlaying) {
                soundManager?.playBackgroundMusic(trackId)
            }
            state.copy(
                workspaceUiState = state.workspaceUiState.copy(
                    selectedTrackId = trackId,
                    musicPosition = 0L
                ),
                currentSession = updatedSession
            )
        }

        viewModelScope.launch {
            updateSession(updatedSession)

            insertEvent(
                LearningSessionEvent(
                    sessionId = updatedSession.sessionId,
                    eventType = LearningSessionEventType.MUSIC_CHANGED,
                    remainingSeconds = _uiState.value.timerUiState.timeLeft,
                    metadata = mapOf(
                        "source" to "workspace",
                        "action" to if (isMusicPlaying) "music_play" else "music_pause",
                        "track_id" to trackId
                    )
                )
            )
        }
    }

    fun toggleAmbientSound(soundId: String) {
        if (soundManager == null) return

        val currentState = _uiState.value
        val currentWorkspace = currentState.workspaceUiState
        val currentSession = currentState.currentSession
        val remainingSeconds = currentState.timerUiState.timeLeft

        val isCurrentlyActive = currentWorkspace.activeAmbientSoundIds.contains(soundId)

        val newActiveIds = if (isCurrentlyActive) {
            currentWorkspace.activeAmbientSoundIds - soundId
        } else {
            currentWorkspace.activeAmbientSoundIds + soundId
        }

        if (isCurrentlyActive) {
            soundManager.stopAmbientSound(soundId)
        } else {
            soundManager.playAmbientSound(soundId)
        }

        val updatedSession = currentSession.copy(
            lastAmbientSounds = newActiveIds
        )

        _uiState.update { state ->
            state.copy(
                workspaceUiState = state.workspaceUiState.copy(
                    activeAmbientSoundIds = newActiveIds
                ),
                currentSession = updatedSession
            )
        }

        viewModelScope.launch {
            updateSession(updatedSession)

            insertEvent(
                LearningSessionEvent(
                    sessionId = updatedSession.sessionId,
                    eventType = LearningSessionEventType.AMBIENT_CHANGED,
                    remainingSeconds = remainingSeconds,
                    metadata = mapOf(
                        "source" to "workspace",
                        "action" to if (isCurrentlyActive) {
                            "turn_off_ambient"
                        } else {
                            "turn_on_ambient"
                        },
                        "ambient_id" to soundId
                    )
                )
            )
        }
    }

    fun selectBackground(backgroundId: String) {
        val currentState = _uiState.value
        val currentSession = currentState.currentSession
        val remainingSeconds = currentState.timerUiState.timeLeft

        val updatedSession = currentSession.copy(
            lastBackgroundId = backgroundId
        )

        _uiState.update { state ->
            state.copy(
                workspaceUiState = state.workspaceUiState.copy(
                    selectedBackgroundId = backgroundId
                ),
                currentSession = updatedSession
            )
        }

        viewModelScope.launch {
            updateSession(updatedSession)

            insertEvent(
                LearningSessionEvent(
                    sessionId = updatedSession.sessionId,
                    eventType = LearningSessionEventType.BACKGROUND_CHANGED,
                    remainingSeconds = remainingSeconds,
                    metadata = mapOf(
                        "source" to "workspace",
                        "action" to "choose_background",
                        "background_id" to backgroundId
                    )
                )
            )

            repository.saveUserSettings(
                repository.getUserSettings().copy(
                    personalSelectedBackgroundId = backgroundId
                )
            )
        }
    }

    fun updateBackgroundConfig(config: BackgroundConfig) {
        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    backgroundConfig = config
                )
            )
        }
    }

    fun toggleCompactMode() {
        _uiState.update { state ->
            val newValue = !state.workspaceUiState.isCompactMode
            state.copy(
                workspaceUiState = state.workspaceUiState.copy(
                    isCompactMode = newValue,
                    isCompactMenuExpanded = false
                )
            )
        }
    }

    fun toggleCompactMenu() {
        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    isCompactMenuExpanded = !it.workspaceUiState.isCompactMenuExpanded
                )
            )
        }
    }

    fun setActiveCompactSection(section: CompactSection?) {
        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    activeCompactSection = section,
                    isCompactMenuExpanded = false
                )
            )
        }
    }

    fun toggleNotificationEnabled() {
        _uiState.update { state ->

            val newValue = !state.workspaceUiState.isNotificationEnabled
            viewModelScope.launch {
                repository.let { repo ->
                    repo.saveUserSettings(
                        repo.getUserSettings().copy(isNotificationEnabled = newValue)
                    )
                }
            }
            state.copy(
                workspaceUiState = state.workspaceUiState.copy(
                    isNotificationEnabled = newValue
                )
            )
        }
    }

    fun setLearningStyle(style: LearningStyle) {
        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    learningStyle = style
                )
            )
        }
    }

    fun setLearningGroupConfig(config: LearningGroupConfig?) {
        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    learningGroupConfig = config
                )
            )
        }
    }

    fun toggleSettings() {
        val currentlyVisible = _uiState.value.workspaceUiState.isSettingsVisible
        if (!currentlyVisible) {
            viewModelScope.launch {
                val currentSettings = repository.getUserSettings()
                _uiState.update {
                    it.copy(
                        workspaceUiState = it.workspaceUiState.copy(
                            isSettingsVisible = true,
                            editingWorkMinutes = currentSettings.personalWorkMinutes.toString(),
                            editingBreakMinutes = currentSettings.personalBreakMinutes.toString(),
                            settingsError = ""
                        )
                    )
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    workspaceUiState = it.workspaceUiState.copy(
                        isSettingsVisible = false
                    )
                )
            }
        }
    }

    fun onWorkMinutesChange(value: String) {
        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    editingWorkMinutes = value,
                    settingsError = ""
                )
            )
        }
    }

    fun onBreakMinutesChange(value: String) {
        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    editingBreakMinutes = value,
                    settingsError = ""
                )
            )
        }
    }

    fun saveSettings(onSettingsSaved: ((Int, Int) -> Unit)? = null) {
        val state = _uiState.value
        val currentSession = state.currentSession

        val workMin = state.workspaceUiState.editingWorkMinutes.trim().toIntOrNull()
        val breakMin = state.workspaceUiState.editingBreakMinutes.trim().toIntOrNull()

        if (workMin == null || breakMin == null) {
            _uiState.update {
                it.copy(
                    workspaceUiState = it.workspaceUiState.copy(
                        settingsError = "Vui lòng nhập số hợp lệ"
                    )
                )
            }
            return
        }

        if (workMin !in 1..120) {
            _uiState.update {
                it.copy(
                    workspaceUiState = it.workspaceUiState.copy(
                        settingsError = "Thời gian tập trung: 1 - 120 phút"
                    )
                )
            }
            return
        }

        if (breakMin !in 1..60) {
            _uiState.update {
                it.copy(
                    workspaceUiState = it.workspaceUiState.copy(
                        settingsError = "Thời gian nghỉ: 1 - 60 phút"
                    )
                )
            }
            return
        }

        val updatedSession = currentSession.copy(
            status = LearningSessionStatus.PAUSED,
            plannedWorkMinutes = workMin,
            plannedBreakMinutes = breakMin,
        )

        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    isSettingsVisible = false,
                    settingsError = "",
                    editingWorkMinutes = workMin.toString(),
                    editingBreakMinutes = breakMin.toString()
                )
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
                        workspaceUiState = it.workspaceUiState.copy(
                            settingsError = "Không thể lưu cài đặt. Vui lòng thử lại: ${e.message}",
                            isSettingsVisible = true
                        )
                    )
                }
            }
        }
    }

    fun resetSettingsToDefault() {
        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    editingWorkMinutes = AppConfig.DEFAULT_WORK_MINUTES.toString(),
                    editingBreakMinutes = AppConfig.DEFAULT_BREAK_MINUTES.toString(),
                    settingsError = ""
                )
            )
        }
    }

    fun toggleExitModal() {
        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    isExitModalVisible = !it.workspaceUiState.isExitModalVisible
                )
            )
        }
    }

    fun endSession(onComplete: () -> Unit) {
        val session = _uiState.value.currentSession
        val now = Clock.System.now().toEpochMilliseconds()

        // Calculate Focus Score
        val scoreResult = FocusScoreCalculator.calculate(
            session = session,
            pausedCount = session.pausedCount,
            skippedCount = session.skipCount,
        )

        val updatedSession = session.copy(
            status = LearningSessionStatus.COMPLETED,
            focusScore = scoreResult.score,
            endedAtMillis = now,
            updatedAtMillis = now
        )

        _uiState.update {
            it.copy(
                currentSession = updatedSession,
                workspaceUiState = it.workspaceUiState.copy(
                    focusScoreResult = scoreResult
                )
            )
        }

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
                            "score" to scoreResult.score.toString()
                        )
                    )
                )
                val userSettings = repository.getUserSettings()
                repository.saveUserSettings(
                    userSettings.copy(currentSessionId = null)
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        workspaceUiState = it.workspaceUiState.copy(
                            settingsError = "Không thể kết thúc phiên học. Vui lòng thử lại: ${e.message}"
                        )
                    )
                }
            }
        }
    }

    fun dismissFocusScore(onComplete: () -> Unit) {
        _uiState.update {
            it.copy(
                workspaceUiState = it.workspaceUiState.copy(
                    focusScoreResult = null
                )
            )
        }
        onComplete()
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
                        workspaceUiState = it.workspaceUiState.copy(
                            settingsError = "Không thể tạm dừng phiên học. Vui lòng thử lại: ${e.message}"
                        )
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
                        workspaceUiState = it.workspaceUiState.copy(
                            settingsError = "Không thể xóa phiên học. Vui lòng thử lại: ${e.message}"
                        )
                    )
                }
            }
        }
    }

    fun onNewTaskTextChange(text: String) {
        _uiState.update {
            it.copy(
                tasksUiState = it.tasksUiState.copy(
                    newTaskText = text
                )
            )
        }
    }

    fun addTask() {
        val currentState = _uiState.value
        val currentSession = currentState.currentSession
        val text = _uiState.value.tasksUiState.newTaskText

        if (text.isNotBlank()) {
            val newSessionTask = SessionTask(
                sessionId = currentSession.sessionId,
                title = text
            )

            _uiState.update {
                it.copy(
                    tasksUiState = it.tasksUiState.copy(
                        sessionTasks = it.tasksUiState.sessionTasks + newSessionTask,
                        newTaskText = ""
                    )
                )
            }

            viewModelScope.launch {
                insertTask(newSessionTask)
                insertEvent(
                    LearningSessionEvent(
                        sessionId = currentSession.sessionId,
                        eventType = LearningSessionEventType.ADD_TASK,
                        metadata = mapOf(
                            "source" to "workspace",
                            "action" to "add_new_task",
                            "task_id" to newSessionTask.taskId
                        )
                    )
                )
            }
        }
    }

    fun deleteTask(taskId: String) {
        val currentState = _uiState.value
        val currentSession = currentState.currentSession

        _uiState.update { state ->
            state.copy(
                tasksUiState = state.tasksUiState.copy(
                    sessionTasks = state.tasksUiState.sessionTasks.filter { it.taskId != taskId }
                )
            )
        }

        viewModelScope.launch {
            deleteTask(taskId, currentSession.sessionId)
            insertEvent(
                LearningSessionEvent(
                    sessionId = currentSession.sessionId,
                    eventType = LearningSessionEventType.REMOVE_TASK,
                    metadata = mapOf(
                        "source" to "workspace",
                        "action" to "remove_a_task",
                        "task_id" to taskId
                    )
                )
            )
        }
    }

    fun toggleTask(taskId: String) {
        var updatedSessionTask: SessionTask? = null
        val currentSession = _uiState.value.currentSession

        _uiState.update { state ->
            state.copy(
                tasksUiState = state.tasksUiState.copy(
                    sessionTasks = state.tasksUiState.sessionTasks.map { task ->
                        if (task.taskId == taskId) {
                            val newStatus = !task.isCompleted
                            updatedSessionTask = task.copy(
                                isCompleted = newStatus,
                                completedAtMillis = if (newStatus) Clock.System.now().toEpochMilliseconds() else null
                            )
                            updatedSessionTask
                        } else {
                            task
                        }
                    }
                )
            )
        }

        viewModelScope.launch {
            if (updatedSessionTask != null) {
                updateTask(updatedSessionTask)
                insertEvent(
                    LearningSessionEvent(
                        sessionId = currentSession.sessionId,
                        eventType = LearningSessionEventType.COMPLETED_TASK,
                        metadata = mapOf(
                            "source" to "workspace",
                            "action" to "completed_a_task",
                            "task_id" to taskId
                        )
                    )
                )
            }
        }
    }

    fun toggleTasksExpanded() {
        _uiState.update {
            it.copy(
                tasksUiState = it.tasksUiState.copy(
                    isTasksExpanded = !it.tasksUiState.isTasksExpanded
                )
            )
        }
    }

    private fun showTimerCompletedNotification() {
        val state = _uiState.value

        if (!state.workspaceUiState.isNotificationEnabled) return

        val userSettings = repository.getUserSettings()

        if (userSettings.isNotificationEnabled) {
            when (state.currentMode) {
                PomodoroMode.WORK -> {
                    notificationManager?.showNotification(
                        title = "Work round completed",
                        message = "Bạn đã hoàn thành một phiên học. Đến giờ nghỉ!"
                    )
                }

                PomodoroMode.SHORT_BREAK -> {
                    notificationManager?.showNotification(
                        title = "Break ended",
                        message = "Hết giờ nghỉ. Quay lại tập trung nào!"
                    )
                }

                PomodoroMode.LONG_BREAK -> {
                    notificationManager?.showNotification(
                        title = "Long break ended",
                        message = "Hết giờ nghỉ dài. Sẵn sàng học tiếp!"
                    )
                }
            }
        }
    }

    suspend fun insertEvent(event: LearningSessionEvent) = learningSessionManager.insertEvent(event)

    suspend fun updateSession(session: LearningSessionRecord) = learningSessionManager.updateSession(session)

    suspend fun insertTask(task: SessionTask) = learningSessionManager.insertTask(task, _uiState.value.currentSession.sessionId)

    suspend fun updateTask(task: SessionTask) = learningSessionManager.updateTask(task)

    suspend fun deleteTask(taskId: String, sessionId: String) = learningSessionManager.deleteTaskById(taskId, sessionId)
}