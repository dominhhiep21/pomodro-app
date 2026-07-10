package thong.kotlin.pomodoro.features.focus.score.domain

import thong.kotlin.pomodoro.features.focus.score.domain.FocusScoreCalculator.calculateSessionFocusScore

data class PomodoroRoundResult(
    val roundId: String,
    val sessionId: String,
    val taskId: String,
    val workRound: Int,
    val focusScore: Int?,
    val focusSeconds: Long,
    val pauseCount: Int,
    val pausedSeconds: Long,
    val completed: Boolean,
    val skipped: Boolean,
    val taskSwitched: Boolean,
    val createdAtMillis: Long
)

data class PomodoroScoreItem(
    val score: Int,
    val focusSeconds: Long
)

data class FocusScoreInput(
    var plannedWorkSeconds: Long = 0,
    var elapsedSeconds: Long = 0,
    var isWorkFullCompleted: Boolean = false,
    var wasWorkSkipped: Boolean = false,
    var hasValidTask: Boolean = false,
    var hasAllUnfinishedTasksWhenSkipped: Boolean = false,
    var hasSomeUnfinishedTasksWhenSkipped: Boolean = false,
    var completedTaskCountWhenSkipped: Int = 0,
    var uncompletedTaskCountWhenSkipped: Int = 0,
    var switchTaskCount: Int = 0,
    var pauseCountBeforeAllTasksDone: Int = 0,
    var pausedSecondsBeforeAllTasksDone: Long = 0,
    var journalState: JournalState = JournalState.SKIPPED
)

enum class JournalState {
    FULL,
    PARTIAL,
    SKIPPED
}

fun FocusScoreInput.calculatePoint(): Int {
    return calculateSessionFocusScore(this) ?: 0
}