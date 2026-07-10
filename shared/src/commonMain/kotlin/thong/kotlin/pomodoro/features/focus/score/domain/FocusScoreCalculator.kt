package thong.kotlin.pomodoro.features.focus.score.domain

import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import kotlin.math.max

object FocusScoreCalculator {

    fun calculateSessionFocusScore(input: FocusScoreInput): Int? {
        if (!input.hasValidTask) return null

        if (input.wasWorkSkipped && input.hasAllUnfinishedTasksWhenSkipped) {
            return null
        }

        val timeScore = when {
            input.isWorkFullCompleted -> 45

            input.wasWorkSkipped && input.hasSomeUnfinishedTasksWhenSkipped -> {
                val elapsedRatio = if (input.plannedWorkSeconds > 0) {
                    input.elapsedSeconds.toDouble() / input.plannedWorkSeconds.toDouble()
                } else {
                    0.0
                }.coerceIn(0.0, 1.0)
                (25 * elapsedRatio).toInt()
            }

            input.wasWorkSkipped && !input.hasSomeUnfinishedTasksWhenSkipped -> {
                val elapsedRatio = if (input.plannedWorkSeconds > 0) {
                    input.elapsedSeconds.toDouble() / input.plannedWorkSeconds.toDouble()
                } else {
                    0.0
                }.coerceIn(0.0, 1.0)
                (35 * elapsedRatio).toInt()
            }

            else -> return null
        }

        val taskScore = 15

        val taskStabilityScore = when {
            input.switchTaskCount <= 0 -> 10
            input.switchTaskCount == 1 -> 7
            input.switchTaskCount == 2 -> 4
            else -> 0
        }

        val pauseCountScore = when {
            input.pauseCountBeforeAllTasksDone <= 0 -> 10
            input.pauseCountBeforeAllTasksDone == 1 -> 8
            input.pauseCountBeforeAllTasksDone == 2 -> 6
            input.pauseCountBeforeAllTasksDone == 3 -> 3
            else -> 0
        }

        val pauseDurationScore = when {
            input.pausedSecondsBeforeAllTasksDone <= 0 -> 10
            input.pausedSecondsBeforeAllTasksDone <= 60 -> 8
            input.pausedSecondsBeforeAllTasksDone <= 180 -> 6
            input.pausedSecondsBeforeAllTasksDone <= 300 -> 3
            else -> 0
        }

        val journalScore = when (input.journalState) {
            JournalState.FULL -> 10
            JournalState.PARTIAL -> 5
            JournalState.SKIPPED -> 0
        }

        val unfinishedTaskPenalty = calculateUnfinishedTaskPenalty(
            totalTaskCount = input.completedTaskCountWhenSkipped + input.uncompletedTaskCountWhenSkipped,
            unfinishedTaskCount = input.completedTaskCountWhenSkipped,
            applyPenalty = input.wasWorkSkipped && input.hasSomeUnfinishedTasksWhenSkipped
        )

        return (timeScore + taskScore + taskStabilityScore + pauseCountScore + pauseDurationScore + journalScore - unfinishedTaskPenalty)
            .coerceIn(0, 100)
    }

    private fun calculateUnfinishedTaskPenalty(
        totalTaskCount: Int,
        unfinishedTaskCount: Int,
        applyPenalty: Boolean
    ): Int {
        if (!applyPenalty) return 0
        if (totalTaskCount <= 0) return 0
        val unfinishedRatio = unfinishedTaskCount.toDouble() / totalTaskCount.toDouble()
        return (10 * unfinishedRatio.coerceIn(0.0, 1.0)).toInt()
    }

    /**
     * Calculate Focus Score based on formula:
     * Focus Score = 100 - (pausedCount * 5) - (skippedCount * 10) - (percentIncomplete * 100)
     */
    fun calculate(
        session: LearningSessionRecord,
        pausedCount: Int,
        skippedCount: Int
    ): FocusScoreResult {
        // 1. Completion Penalty
        val totalPlannedSeconds = session.plannedWorkMinutes * 60
        val focusSeconds = session.totalFocusSeconds

        val percentIncomplete = if (totalPlannedSeconds > 0) {
            max(0f, (totalPlannedSeconds - focusSeconds).toFloat() / totalPlannedSeconds)
        } else 0f

        // 2. Base Calculation
        var finalScore = 100
        finalScore -= (pausedCount * 5)
        finalScore -= (skippedCount * 10)
        finalScore -= (percentIncomplete * 100).toInt()

        // Clamp score between 0 and 100
        finalScore = finalScore.coerceIn(0, 100)

        // 3. Generate Feedback
        val feedback = generateFeedback(finalScore, pausedCount, skippedCount, session.status)

        return FocusScoreResult(
            score = finalScore,
            feedback = feedback,
            pausedCount = pausedCount,
            skippedCount = skippedCount,
            completionRate = 1f - percentIncomplete,
            actualFocusMinutes = focusSeconds / 60,
            plannedFocusMinutes = session.plannedWorkMinutes
        )
    }

    private fun generateFeedback(
        score: Int,
        pausedCount: Int,
        skippedCount: Int,
        status: LearningSessionStatus
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
}
