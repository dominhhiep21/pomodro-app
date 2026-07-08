package thong.kotlin.pomodoro.features.focus.score.domain

import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord
import thong.kotlin.pomodoro.features.session.domain.LearningSessionStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class
FocusScoreCalculatorTest {

    // --- Helper ---
    private fun createSession(
        plannedWorkMinutes: Int = 25,
        totalFocusSeconds: Int = 0,
        status: LearningSessionStatus = LearningSessionStatus.COMPLETED
    ): LearningSessionRecord {
        return LearningSessionRecord(
            sessionId = "test-session",
            plannedWorkMinutes = plannedWorkMinutes,
            totalFocusSeconds = totalFocusSeconds,
            status = status
        )
    }

    // --- Score Calculation Tests ---

    @Test
    fun calculate_fullFocusNoPauseNoSkip_returns100() {
        // Given
        val session = createSession(plannedWorkMinutes = 25, totalFocusSeconds = 1500)

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 0, skippedCount = 0)

        // Then
        val expectedScore = 100
        assertEquals(expectedScore, actualResult.score)
        assertEquals(1.0f, actualResult.completionRate)
    }

    @Test
    fun calculate_pausedMultipleTimes_deducts5PerPause() {
        // Given
        val session = createSession(plannedWorkMinutes = 25, totalFocusSeconds = 1500)

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 3, skippedCount = 0)

        // Then
        val expectedScore = 85 // 100 - (3 * 5)
        assertEquals(expectedScore, actualResult.score)
    }

    @Test
    fun calculate_skippedRounds_deducts10PerSkip() {
        // Given
        val session = createSession(plannedWorkMinutes = 25, totalFocusSeconds = 1500)

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 0, skippedCount = 2)

        // Then
        val expectedScore = 80 // 100 - (2 * 10)
        assertEquals(expectedScore, actualResult.score)
    }

    @Test
    fun calculate_halfFocusTime_deductsCompletionPenalty() {
        // Given
        val session = createSession(plannedWorkMinutes = 25, totalFocusSeconds = 750) // 50% complete

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 0, skippedCount = 0)

        // Then
        val expectedScore = 50 // 100 - (0.5 * 100)
        assertEquals(expectedScore, actualResult.score)
        assertEquals(0.5f, actualResult.completionRate)
    }

    @Test
    fun calculate_zeroPlannedMinutes_noCompletionPenalty() {
        // Given
        val session = createSession(plannedWorkMinutes = 0, totalFocusSeconds = 100)

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 0, skippedCount = 0)

        // Then
        val expectedScore = 100
        assertEquals(expectedScore, actualResult.score)
    }

    @Test
    fun calculate_allPenaltiesExceedMax_clampedToZero() {
        // Given
        val session = createSession(plannedWorkMinutes = 25, totalFocusSeconds = 0)

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 10, skippedCount = 5)

        // Then
        val expectedScore = 0
        assertEquals(expectedScore, actualResult.score)
    }

    @Test
    fun calculate_focusExceedsPlanned_completionRateIs1() {
        // Given
        val session = createSession(plannedWorkMinutes = 25, totalFocusSeconds = 2000)

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 0, skippedCount = 0)

        // Then
        val expectedScore = 100
        assertEquals(expectedScore, actualResult.score)
        assertEquals(1.0f, actualResult.completionRate)
    }

    // --- Feedback Tests (tested via calculate) ---

    @Test
    fun calculate_incompleteSessionLowScore_returnsAbandonedFeedback() {
        // Given — session PAUSED (not completed), score will be < 50
        val session = createSession(
            plannedWorkMinutes = 25,
            totalFocusSeconds = 0,
            status = LearningSessionStatus.PAUSED
        )

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 5, skippedCount = 3)

        // Then
        assertTrue(actualResult.feedback.contains("bị bỏ dở"))
    }

    @Test
    fun calculate_score95Plus_returnsExcellentFeedback() {
        // Given
        val session = createSession(plannedWorkMinutes = 25, totalFocusSeconds = 1500)

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 0, skippedCount = 0)

        // Then — score = 100
        assertTrue(actualResult.feedback.contains("Tuyệt vời"))
    }

    @Test
    fun calculate_score85NoPause_returnsHighFocusFeedback() {
        // Given — score = 85: needs 15 deducted from completion penalty
        // totalFocusSeconds = 1500 - (1500 * 0.15) = 1275? No — penalty = (planned - actual) / planned
        // For score = 85 with no pause/skip: percentIncomplete * 100 = 15 → percentIncomplete = 0.15
        // actual = planned * (1 - 0.15) = 1500 * 0.85 = 1275
        val session = createSession(plannedWorkMinutes = 25, totalFocusSeconds = 1275)

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 0, skippedCount = 0)

        // Then — score should be 85, pausedCount = 0
        assertTrue(actualResult.feedback.contains("xuyên suốt phiên học"))
    }

    @Test
    fun calculate_score70HighPause_returnsLimitPauseFeedback() {
        // Given — target score ~70 with pausedCount > 3
        // 100 - (4*5) - 0 - completionPenalty = 70 → completionPenalty = 10 → percentIncomplete = 0.10
        // actualFocus = 1500 * 0.90 = 1350
        val session = createSession(plannedWorkMinutes = 25, totalFocusSeconds = 1350)

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 4, skippedCount = 0)

        // Then
        assertTrue(actualResult.feedback.contains("hạn chế tạm dừng"))
    }

    @Test
    fun calculate_score50WithSkips_returnsLimitSkipFeedback() {
        // Given — target score ~50-69 with skippedCount > 0
        // 100 - 0 - (2*10) - completionPenalty = ~55 → completionPenalty = 25 → percentIncomplete = 0.25
        // actualFocus = 1500 * 0.75 = 1125
        val session = createSession(plannedWorkMinutes = 25, totalFocusSeconds = 1125)

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 0, skippedCount = 2)

        // Then
        assertTrue(actualResult.feedback.contains("hạn chế bỏ qua"))
    }

    @Test
    fun calculate_scoreBelowAll_returnsLowScoreFeedback() {
        // Given — score < 50, status = COMPLETED
        // 100 - (2*5) - (1*10) - 50 (50% incomplete) = 30
        val session = createSession(
            plannedWorkMinutes = 25,
            totalFocusSeconds = 750,
            status = LearningSessionStatus.COMPLETED
        )

        // When
        val actualResult = FocusScoreCalculator.calculate(session, pausedCount = 2, skippedCount = 1)

        // Then — score = 100 - 10 - 10 - 50 = 30
        assertTrue(actualResult.feedback.contains("Điểm tập trung thấp"))
    }
}
