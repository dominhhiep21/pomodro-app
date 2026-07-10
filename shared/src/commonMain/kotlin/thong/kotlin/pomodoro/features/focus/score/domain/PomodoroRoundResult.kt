package thong.kotlin.pomodoro.features.focus.score.domain

import thong.kotlin.pomodoro.features.focus.score.domain.FocusScoreCalculator.calculateSessionFocusScore
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import kotlin.time.Clock

data class PomodoroRoundResult(
    val sessionId: String,
    val roundId: String,
    val focusScore: Int,
    val focusSeconds: Int,
    val completed: Boolean,
    val skipped: Boolean,
    val pauseCount: Int,
    val pausedSeconds: Int,
    val switchTaskCount: Int,
    val createdAtMillis: Long = Clock.System.now().toEpochMilliseconds()
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

fun FocusScoreInput.toResult(sessionId: String, roundId: Int): PomodoroRoundResult {
    return PomodoroRoundResult(
        sessionId = sessionId,
        roundId = roundId.toString(),
        focusScore = calculatePoint(),
        focusSeconds = (plannedWorkSeconds - elapsedSeconds).toInt(),
        completed = isWorkFullCompleted,
        skipped = wasWorkSkipped,
        pauseCount = pauseCountBeforeAllTasksDone,
        pausedSeconds = pausedSecondsBeforeAllTasksDone.toInt(),
        switchTaskCount = switchTaskCount
    )
}

fun Map<Int, PomodoroRoundResult>.toFocusScoreResult(plannedFocusMinutes: Int): FocusScoreResult {
    val totalRound = values.size
    val rounds = values.toList()
    if (rounds.isEmpty()) {
        return FocusScoreResult(
            score = 0,
            feedback = "Chưa có dữ liệu Pomodoro",
            pausedCount = 0,
            skippedCount = 0,
            completionRate = 0f,
            actualFocusMinutes = 0,
            plannedFocusMinutes = plannedFocusMinutes * totalRound
        )
    }

    val totalFocusSeconds = rounds.sumOf { it.focusSeconds }
    val completedCount = rounds.count { it.completed }
    val skippedCount = rounds.count { it.skipped }
    val totalPauseCount = rounds.sumOf { it.pauseCount }

    val completionRate = completedCount.toFloat() / rounds.size.toFloat()

    /*
    * Tính trung bình Focus Score theo thời gian focus.
    * Round focus lâu hơn sẽ có trọng số lớn hơn.
    */
    val totalWeightedScore = rounds.sumOf { round ->
        round.focusScore.toLong() * round.focusSeconds
    }

    val score = if (totalFocusSeconds > 0) {
        (totalWeightedScore / totalFocusSeconds)
            .toInt()
            .coerceIn(0, 100)
    } else {
        rounds
            .map { it.focusScore }
            .average()
            .toInt()
            .coerceIn(0, 100)
    }

    return FocusScoreResult(
        score = score,
        feedback = generateFeedback(
            score = score,
            pausedCount = totalPauseCount,
            skippedCount = skippedCount
        ),
        pausedCount = totalPauseCount,
        skippedCount = skippedCount,
        completionRate = completionRate.coerceIn(0f, 1f),
        actualFocusMinutes = totalFocusSeconds / 60,
        plannedFocusMinutes = plannedFocusMinutes * totalRound
    )
}

private fun generateFeedback(
    score: Int,
    pausedCount: Int,
    skippedCount: Int,
    status: LearningSessionStatus = LearningSessionStatus.COMPLETED
): String {
    if (status != LearningSessionStatus.COMPLETED && score < 50) {
        return "Phiên học bị bỏ dở. Hãy cố gắng duy trì sự tập trung ở phiên tới nhé!"
    }

    return when {
        score >= 95 -> "Tuyệt vời! Bạn đã có một phiên học cực kỳ hiệu quả và không bị gián đoạn."
        score >= 85 && pausedCount == 0 -> "Rất tốt! Bạn đã duy trì được sự tập trung cao độ xuyên suốt phiên học."
        score >= 85 -> "Rất tốt! Bạn đã duy trì được sự tập trung cao độ."
        score >= 70 && pausedCount > 3 -> "Khá tốt. Tuy nhiên hãy hạn chế tạm dừng để đạt kết quả tốt hơn nhé."
        score >= 70 -> "Khá tốt. Bạn đang đi đúng hướng để rèn luyện sự tập trung."
        score >= 50 && skippedCount > 0 -> "Bạn đã hoàn thành phiên học, nhưng hãy hạn chế bỏ qua các vòng nhé."
        score >= 50 -> "Bạn đã hoàn thành phiên học, nhưng sự tập trung còn bị phân tán."
        else -> "Điểm tập trung thấp. Hãy tìm một không gian yên tĩnh hơn để bắt đầu phiên mới."
    }
}