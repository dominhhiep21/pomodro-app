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
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.SessionRecord
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettings
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepository
import thong.kotlin.pomodoro.features.pomodoro._base.domain.EventType
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroConfig
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode
import kotlin.random.Random

data class TimerUiState(
    val isActive: Boolean = false,
    val timeLeft: Long = 25 * 60L,
    val currentMode: PomodoroMode = PomodoroMode.WORK,
    val config: PomodoroConfig = PomodoroConfig(),
    val pomodorosToday: Int = 0,
    val event: EventType = EventType.NOTHING,
    val pendingNotification: String? = null
)

class TimerViewModel(
    private val soundManager: SoundManager? = null,
    private val repository: UserAppStateRepository? = null
) : ViewModel() {
    // Trạng thái độc lập chỉ dành cho UI Đồng hồ
    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    // Job quản lý vòng lặp đếm ngược
    private var timerJob: Job? = null

    init {
        // Khởi tạo dữ liệu ban đầu (Config và số Pomodoro hôm nay)
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val savedSettings = repository?.getUserSettings() ?: UserSettings()

            _uiState.update { state ->
                state.copy(
                    config = state.config.copy(
                        workMinutes = savedSettings.workMinutes,
                        shortBreakMinutes = savedSettings.breakMinutes
                    ),
                    timeLeft = PomodoroMode.WORK.totalSeconds(
                        PomodoroConfig(savedSettings.workMinutes, savedSettings.breakMinutes)
                    )
                )
            }

            repository?.getTodayStats()?.collect { stats ->
                if (stats != null) {
                    _uiState.update { it.copy(pomodorosToday = stats.sessionsCompleted) }
                }
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

        val (newMode, nextTime, eventType, notification) = if (currentState.currentMode == PomodoroMode.WORK) {
            // Học xong: Cộng điểm và nghỉ
            val nextMode = PomodoroMode.SHORT_BREAK
            val nextTime = nextMode.totalSeconds(currentState.config)

            viewModelScope.launch {
                repository?.saveSession(
                    SessionRecord(
                        id = Random.nextInt().toString(),
                        startTime = getCurrentDateTimeString(),
                        endTime = getCurrentDateTimeString(),
                        mode = PomodoroMode.WORK.name,
                        durationMinutes = currentState.config.workMinutes,
                        status = "COMPLETED",
                        // Note: Bỏ đếm task ở đây vì logic task sẽ do TasksViewModel lo
                        tasksCompletedCount = 0
                    )
                )
                repository?.incrementDailyStats(
                    sessionsCompleted = 1,
                    focusMinutes = currentState.config.workMinutes
                )
            }

            listOf(nextMode, nextTime, EventType.WORK_END, "Work session completed. Time for a break!")
        } else {
            // Nghỉ xong: Quay lại làm việc
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

fun PomodoroMode.totalSeconds(config: PomodoroConfig): Long {
    return when (this) {
        PomodoroMode.WORK -> config.workSeconds.toLong()
        PomodoroMode.SHORT_BREAK -> config.shortBreakSeconds.toLong()
        PomodoroMode.LONG_BREAK -> config.longBreakSeconds.toLong()
    }
}