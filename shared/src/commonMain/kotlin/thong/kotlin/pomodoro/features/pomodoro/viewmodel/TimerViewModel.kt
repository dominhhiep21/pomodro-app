package thong.kotlin.pomodoro.features.pomodoro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.pomodoro._base.domain.EventType
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroConfig
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettingsV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.totalSeconds
import thong.kotlin.pomodoro.features.session.data.LearningSessionManager
import thong.kotlin.pomodoro.features.session.domain.CurrentLearningMode
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEvent
import thong.kotlin.pomodoro.features.session.domain.LearningSessionEventType
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import kotlin.time.Clock

data class TimerUiState(
    val isSessionStarted: Boolean = false,
    val isActive: Boolean = false,
    val timeLeft: Int = 25 * 60,
    val currentMode: PomodoroMode = PomodoroMode.WORK,
    val config: PomodoroConfig = PomodoroConfig(),
    val pomodorosToday: Int = 0,
    val event: EventType = EventType.NOTHING,
    val pendingNotification: String? = null,
    val currentSession: LearningSessionRecord
)

class TimerViewModel(
    private val soundManager: SoundManager? = DependencyRegistry.soundManager,
    private val repository: UserAppStateRepositoryV2? = DependencyRegistry.userAppStateRepositoryV2,
    private val learningSessionManager: LearningSessionManager = DependencyRegistry.learningSessionManager,
    private val currentSession: LearningSessionRecord
) : ViewModel() {
    // Trạng thái độc lập chỉ dành cho UI Đồng hồ
    private val _uiState = MutableStateFlow(TimerUiState(currentSession = currentSession))
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    // Job quản lý vòng lặp đếm ngược
    private var timerJob: Job? = null

    init {
        // Khởi tạo dữ liệu ban đầu (Config và số Pomodoro hôm nay)
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val savedSettings = repository?.getUserSettings() ?: UserSettingsV2()

            _uiState.update { state ->
                state.copy(
                    config = state.config.copy(
                        workMinutes = savedSettings.personalWorkMinutes,
                        shortBreakMinutes = savedSettings.personalBreakMinutes
                    ),
                    timeLeft = PomodoroMode.WORK.totalSeconds(
                        PomodoroConfig(
                            savedSettings.personalWorkMinutes,
                            savedSettings.personalBreakMinutes
                        )
                    )
                )
            }
        }
    }

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

    fun toggleTimer() {
        val state = _uiState.value
        val session = state.currentSession
        val currentMode = state.currentMode
        val totalSeconds = currentMode.totalSeconds(state.config)

        val isStartOfRound = state.timeLeft == totalSeconds
        val isMiddleOfRound = state.timeLeft in 1 until totalSeconds

        val eventType: LearningSessionEventType
        val action: String
        val nextStatus: LearningSessionStatus
        val nextLearningMode: CurrentLearningMode
        val shouldStartTimer: Boolean
        val shouldInsertSessionStartedEvent: Boolean

        when {
            state.isActive && currentMode == PomodoroMode.WORK -> {
                eventType = LearningSessionEventType.WORK_ROUND_PAUSED
                action = "pause_work"
                nextStatus = LearningSessionStatus.PAUSED
                nextLearningMode = CurrentLearningMode.WORK
                shouldStartTimer = false
                shouldInsertSessionStartedEvent = false
            }

            state.isActive && currentMode == PomodoroMode.SHORT_BREAK -> {
                eventType = LearningSessionEventType.BREAK_ROUND_PAUSED
                action = "pause_break"
                nextStatus = LearningSessionStatus.PAUSED
                nextLearningMode = CurrentLearningMode.BREAK
                shouldStartTimer = false
                shouldInsertSessionStartedEvent = false
            }

            !state.isActive && currentMode == PomodoroMode.WORK && isMiddleOfRound -> {
                eventType = LearningSessionEventType.WORK_ROUND_RESUMED
                action = "resume_work"
                nextStatus = LearningSessionStatus.RUNNING
                nextLearningMode = CurrentLearningMode.WORK
                shouldStartTimer = true
                shouldInsertSessionStartedEvent = false
            }

            !state.isActive && currentMode == PomodoroMode.WORK && isStartOfRound -> {
                eventType = LearningSessionEventType.WORK_ROUND_STARTED
                action = "start_work"
                nextStatus = LearningSessionStatus.RUNNING
                nextLearningMode = CurrentLearningMode.WORK
                shouldStartTimer = true
                shouldInsertSessionStartedEvent = !state.isSessionStarted
            }

            !state.isActive && currentMode == PomodoroMode.SHORT_BREAK && isMiddleOfRound -> {
                eventType = LearningSessionEventType.BREAK_ROUND_RESUMED
                action = "resume_break"
                nextStatus = LearningSessionStatus.RUNNING
                nextLearningMode = CurrentLearningMode.BREAK
                shouldStartTimer = true
                shouldInsertSessionStartedEvent = false
            }

            !state.isActive && currentMode == PomodoroMode.SHORT_BREAK && isStartOfRound -> {
                eventType = LearningSessionEventType.BREAK_ROUND_STARTED
                action = "start_break"
                nextStatus = LearningSessionStatus.RUNNING
                nextLearningMode = CurrentLearningMode.BREAK
                shouldStartTimer = true
                shouldInsertSessionStartedEvent = false
            }

            else -> return
        }

        val updatedSession = session.copy(
            status = nextStatus,
            currentLearningMode = nextLearningMode,
            completedWorkRounds = state.pomodorosToday
        )

        if (shouldStartTimer) {
            _uiState.update {
                it.copy(
                    isActive = true,
                    isSessionStarted = it.isSessionStarted || shouldInsertSessionStartedEvent,
                    currentSession = updatedSession
                )
            }

            startTimer()
        } else {
            stopTimerJobOnly()

            _uiState.update {
                it.copy(
                    isActive = false,
                    currentSession = updatedSession
                )
            }
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateSession(updatedSession)

                if (shouldInsertSessionStartedEvent) {
                    insertEvent(
                        LearningSessionEvent(
                            sessionId = session.sessionId,
                            eventType = LearningSessionEventType.SESSION_STARTED,
                            remainingSeconds = state.timeLeft,
                            metadata = mapOf(
                                "source" to "toggle_timer",
                                "action" to "start_session"
                            )
                        )
                    )
                }

                insertEvent(
                    LearningSessionEvent(
                        sessionId = session.sessionId,
                        eventType = eventType,
                        remainingSeconds = state.timeLeft,
                        currentRound = state.pomodorosToday,
                        metadata = mapOf(
                            "source" to "toggle_timer",
                            "action" to action,
                            "mode" to currentMode.name
                        )
                    )
                )
            }
        }
    }

    private fun startTimer() {
        _uiState.update { currentState ->
            currentState.copy(
                isActive = true,
                event = resolveStartEvent(currentState)
            )
        }

        timerJob?.cancel() // Hủy job cũ đề phòng trùng lặp

        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1000) // Đếm lùi 1 giây
                _uiState.update { state ->
                    val newTimeLeft = state.timeLeft - 1

                    // Phát tiếng bíp ở những giây cuối
                    if (newTimeLeft in 1L..4L) {
                        soundManager?.playBeepSound()
                    }

                    state.copy(timeLeft = newTimeLeft)
                }
            }
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

    fun resetTimer() {
        pauseTimer()
        val state = _uiState.value
        val currentSession = state.currentSession
        val totalSeconds = state.currentMode.totalSeconds(state.config)
        val hasStarted = state.timeLeft < totalSeconds
        stopTimerJobOnly()

        val updatedSession = currentSession.copy(
            status = LearningSessionStatus.PAUSED
        )

        _uiState.update {
            it.copy(
                isActive = false,
                timeLeft = totalSeconds,
                event = EventType.NOTHING,
                currentSession = updatedSession
            )
        }

        if (!hasStarted) return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
                        remainingSeconds = state.timeLeft,
                        currentRound = state.pomodorosToday,
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
    }

    fun skipTimer() {
        stopTimerJobOnly()
        val state = _uiState.value
        val currentSession = state.currentSession
        val currentMode = state.currentMode
        val currentTotalSeconds = currentMode.totalSeconds(state.config)

        val elapsedSeconds = currentTotalSeconds - state.timeLeft

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
                    totalFocusSeconds = currentSession.totalFocusSeconds + elapsedSeconds
                )
            }

            PomodoroMode.SHORT_BREAK,
            PomodoroMode.LONG_BREAK -> {
                currentSession.copy(
                    status = LearningSessionStatus.PAUSED,
                    currentLearningMode = nextLearningMode,
                    totalBreakSeconds = currentSession.totalBreakSeconds + elapsedSeconds
                )
            }
        }

        _uiState.update {
            it.copy(
                isActive = false,
                currentMode = nextMode,
                timeLeft = nextMode.totalSeconds(it.config),
                event = EventType.NOTHING,
                currentSession = updatedSession
            )
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
                        remainingSeconds = state.timeLeft,
                        currentRound = state.pomodorosToday,
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
    }

    private fun handleTimerComplete() {
        soundManager?.playChimeSound()
        stopTimerJobOnly()
        val state = _uiState.value
        val session = state.currentSession
        val currentMode = state.currentMode

        val isWorkMode = currentMode == PomodoroMode.WORK

        val nextMode = if (isWorkMode) {
            PomodoroMode.SHORT_BREAK
        } else {
            PomodoroMode.WORK
        }

        val nextTime = nextMode.totalSeconds(state.config)

        val updatedSession = if (isWorkMode) {
            session.copy(
                status = LearningSessionStatus.PAUSED,
                currentLearningMode = CurrentLearningMode.BREAK,
                completedWorkRounds = session.completedWorkRounds + 1,
                totalFocusSeconds = session.totalFocusSeconds + PomodoroMode.WORK.totalSeconds(state.config)
            )
        } else {
            session.copy(
                status = LearningSessionStatus.PAUSED,
                currentLearningMode = CurrentLearningMode.WORK,
                completedBreakRounds = session.completedBreakRounds + 1,
                totalBreakSeconds = session.totalBreakSeconds + currentMode.totalSeconds(state.config)
            )
        }

        val eventType = if (isWorkMode) {
            LearningSessionEventType.WORK_ROUND_COMPLETED
        } else {
            LearningSessionEventType.BREAK_ROUND_ENDED
        }

        val currentRound = if (isWorkMode) {
            updatedSession.completedWorkRounds
        } else {
            updatedSession.completedBreakRounds
        }

        val result = if (isWorkMode) {
            TimerCompleteResult(
                newMode = nextMode,
                nextTime = nextTime,
                eventType = EventType.WORK_END,
                notification = "Work session completed. Time for a break!"
            )
        } else {
            TimerCompleteResult(
                newMode = nextMode,
                nextTime = nextTime,
                eventType = EventType.BREAK_END,
                notification = "Break finished. Time to focus again!"
            )
        }

        _uiState.update {
            it.copy(
                isActive = false,
                pomodorosToday = if (isWorkMode) {
                    it.pomodorosToday + 1
                } else {
                    it.pomodorosToday
                },
                currentMode = result.newMode,
                timeLeft = result.nextTime,
                event = result.eventType,
                pendingNotification = result.notification,
                currentSession = updatedSession
            )
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                learningSessionManager.updateSession(updatedSession)
                learningSessionManager.insertEvent(
                    LearningSessionEvent(
                        eventId = "auto_event_${Clock.System.now().toEpochMilliseconds()}",
                        sessionId = session.sessionId,
                        eventType = eventType,
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
            }
        }
    }

    private fun stopTimerJobOnly() {
        timerJob?.cancel()
        timerJob = null
    }

    fun getSessionById(): LearningSessionRecord? = learningSessionManager.getSessionById(currentSession.sessionId)

    fun insertEvent(event: LearningSessionEvent) = learningSessionManager.insertEvent(event)

    fun updateSession(session: LearningSessionRecord) {
        learningSessionManager.updateSession(session)
        _uiState.update { it.copy(currentSession = getSessionById()!!) }
    }

    fun clearPendingNotification() {
        _uiState.update { it.copy(pendingNotification = null) }
    }

    // --- HELPER LOGIC ---
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
}

private data class TimerCompleteResult(
    val newMode: PomodoroMode,
    val nextTime: Int,
    val eventType: EventType,
    val notification: String
)