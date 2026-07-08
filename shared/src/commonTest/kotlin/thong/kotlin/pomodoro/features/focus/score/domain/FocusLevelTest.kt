package thong.kotlin.pomodoro.features.focus.score.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class FocusLevelTest {

    @Test
    fun fromScore_score90_returnsExcellent() {
        // Given
        val score = 90

        // When
        val actualLevel = FocusLevel.fromScore(score)

        // Then
        assertEquals(FocusLevel.EXCELLENT, actualLevel)
    }

    @Test
    fun fromScore_score100_returnsExcellent() {
        // Given
        val score = 100

        // When
        val actualLevel = FocusLevel.fromScore(score)

        // Then
        assertEquals(FocusLevel.EXCELLENT, actualLevel)
    }

    @Test
    fun fromScore_score89_returnsGood() {
        // Given
        val score = 89

        // When
        val actualLevel = FocusLevel.fromScore(score)

        // Then
        assertEquals(FocusLevel.GOOD, actualLevel)
    }

    @Test
    fun fromScore_score75_returnsGood() {
        // Given
        val score = 75

        // When
        val actualLevel = FocusLevel.fromScore(score)

        // Then
        assertEquals(FocusLevel.GOOD, actualLevel)
    }

    @Test
    fun fromScore_score50_returnsAverage() {
        // Given
        val score = 50

        // When
        val actualLevel = FocusLevel.fromScore(score)

        // Then
        assertEquals(FocusLevel.AVERAGE, actualLevel)
    }

    @Test
    fun fromScore_score30_returnsPoor() {
        // Given
        val score = 30

        // When
        val actualLevel = FocusLevel.fromScore(score)

        // Then
        assertEquals(FocusLevel.POOR, actualLevel)
    }

    @Test
    fun fromScore_score0_returnsPoor() {
        // Given
        val score = 0

        // When
        val actualLevel = FocusLevel.fromScore(score)

        // Then
        assertEquals(FocusLevel.POOR, actualLevel)
    }
}
