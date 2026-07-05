package thong.kotlin.pomodoro.features.focus.score.domain

import kotlinx.serialization.Serializable

@Serializable
data class FocusScoreResult(
    val score: Int,                // 0 - 100
    val feedback: String,          // Short comment based on score
    val pausedCount: Int,
    val skippedCount: Int,
    val completionRate: Float,     // 0.0 - 1.0
    val actualFocusMinutes: Int,
    val plannedFocusMinutes: Int
)

enum class FocusLevel(val label: String, val minScore: Int) {
    EXCELLENT("Tuyệt vời", 90),
    GOOD("Tốt", 75),
    AVERAGE("Trung bình", 50),
    POOR("Kém", 0);

    companion object {
        fun fromScore(score: Int): FocusLevel {
            return entries.filter { score >= it.minScore }.maxByOrNull { it.minScore } ?: POOR
        }
    }
}
