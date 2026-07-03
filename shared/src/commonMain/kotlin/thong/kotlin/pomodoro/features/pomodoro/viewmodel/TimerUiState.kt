package thong.kotlin.pomodoro.features.pomodoro.viewmodel

import kotlinx.coroutines.Job
import thong.kotlin.pomodoro.features.pomodoro._base.domain.EventType
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroConfig
import thong.kotlin.pomodoro.features.pomodoro._base.domain.PomodoroMode

data class TimerUiState(
    val isSessionStarted: Boolean = false,
    val isJustEndedBreak: Boolean = false,
    val isActive: Boolean = false,
    val timeLeft: Int,
    val config: PomodoroConfig,
    val pomodorosToday: Int = 0,
    val event: EventType = EventType.NOTHING,
    val pendingNotification: String? = null
)

data class TimerCompleteResult(
    val newMode: PomodoroMode,
    val nextTime: Int,
    val eventType: EventType,
    val notification: String
)

fun stopTimerJobOnly(timerJob: Job?) {
    timerJob?.cancel()
}