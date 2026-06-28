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
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.core.utils.getCurrentDateTimeString
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.pomodoro._base.domain.EventType
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroConfig
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.SessionRecord
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettingsV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.totalSeconds
import thong.kotlin.pomodoro.features.session.data.LearningSessionManager
import thong.kotlin.pomodoro.features.session.domain.CurrentLearningMode
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import kotlin.random.Random
import kotlin.time.Clock

data class TimerUiState(
    val isActive: Boolean = false,
    val timeLeft: Long = 25 * 60L,
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

//            repository?.getTodayStats()?.collect { stats ->
//                if (stats != null) {
//                    _uiState.update { it.copy(pomodorosToday = stats.sessionsCompleted) }
//                }
//            }
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
        _uiState.update {
            it.copy(
                isActive = false,
                timeLeft = it.currentMode.totalSeconds(it.config),
                event = EventType.NOTHING
            )
        }
    }

    fun skipTimer() {
        timerJob?.cancel()
        val currentState = _uiState.value

        if (currentState.currentMode == PomodoroMode.WORK) {
            // Đang học mà Skip -> Chuyển sang nghỉ, KHÔNG cộng điểm
            _uiState.update {
                it.copy(
                    isActive = false,
                    currentMode = PomodoroMode.SHORT_BREAK,
                    timeLeft = PomodoroMode.SHORT_BREAK.totalSeconds(it.config),
                    event = EventType.NOTHING
                )
            }
        } else {
            // Đang nghỉ mà Skip -> Vào học phiên mới
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

    private fun handleTimerComplete() {
        soundManager?.playChimeSound()
        pauseTimer()
        val currentState = _uiState.value
        val currentSession = currentState.currentSession

        val result = if (currentState.currentMode == PomodoroMode.WORK) {
            val nextMode = PomodoroMode.SHORT_BREAK
            val nextTime = nextMode.totalSeconds(currentState.config)

            viewModelScope.launch {
                learningSessionManager.updateSession(
                    currentSession.copy(
                        currentLearningMode = CurrentLearningMode.BREAK,
                        completedWorkRounds = currentSession.completedWorkRounds + 1,
                        totalFocusSeconds = currentSession.totalFocusSeconds +
                                currentState.config.workMinutes * 60
                    )
                )
//                learningSessionManager.incrementDailyStats(
//                    sessionsCompleted = 1,
//                    focusMinutes = currentState.config.workMinutes
//                )
            }
            TimerCompleteResult(
                newMode = nextMode,
                nextTime = nextTime,
                eventType = EventType.WORK_END,
                notification = "Work session completed. Time for a break!"
            )
        } else {
            val nextMode = PomodoroMode.WORK
            val nextTime = nextMode.totalSeconds(currentState.config)

            viewModelScope.launch {
                learningSessionManager.updateSession(
                    currentSession.copy(
                        currentLearningMode = CurrentLearningMode.WORK,
                        completedBreakRounds = currentSession.completedBreakRounds + 1,
                        totalBreakSeconds = currentSession.totalBreakSeconds +
                                currentState.config.shortBreakMinutes * 60
                    )
                )

//                learningSessionManager.incrementDailyStats(
//                    breakMinutes = currentState.config.shortBreakMinutes
//                )
            }

            TimerCompleteResult(
                newMode = nextMode,
                nextTime = nextTime,
                eventType = EventType.BREAK_END,
                notification = "Break finished. Time to focus again!"
            )
        }

        _uiState.update { state ->
            val nextLearningMode = if (result.newMode == PomodoroMode.WORK) {
                CurrentLearningMode.WORK
            } else {
                CurrentLearningMode.BREAK
            }
            state.copy(
                pomodorosToday = if (result.eventType == EventType.WORK_END) {
                    state.pomodorosToday + 1
                } else {
                    state.pomodorosToday
                },
                currentMode = result.newMode,
                timeLeft = result.nextTime,
                event = result.eventType,
                pendingNotification = result.notification,
                currentSession = state.currentSession.copy(
                    currentLearningMode = nextLearningMode
                )
            )
        }
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
    val nextTime: Long,
    val eventType: EventType,
    val notification: String
)