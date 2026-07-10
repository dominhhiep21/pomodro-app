package thong.kotlin.pomodoro.features.pomodoro.timer.domain

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class TimerPauseRecord(
    val pauseId: String = "timer_pause_${Clock.System.now().toEpochMilliseconds()}",
    val sessionId: String,
    val pomodoroSession: Int = 0,
    val startedAtMillis: Long,
    val endedAtMillis: Long? = null
)

fun List<TimerPauseRecord>.totalPausedSeconds(): Long =
    sumOf { record ->
        val endedAt = record.endedAtMillis ?: return@sumOf 0L
        ((endedAt - record.startedAtMillis) / 1_000L)
            .coerceAtLeast(0L)
    }