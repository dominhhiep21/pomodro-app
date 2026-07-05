package thong.kotlin.pomodoro.features.focus.score.domain

import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import kotlin.math.max

object FocusScoreCalculator {

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
