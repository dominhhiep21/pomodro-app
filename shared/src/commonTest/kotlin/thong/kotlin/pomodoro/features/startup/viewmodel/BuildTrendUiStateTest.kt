package thong.kotlin.pomodoro.features.startup.viewmodel

import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro._base.domain.StatCardType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for buildTrendUiState() — the pure function that generates trend display text
 * and color for the Home dashboard stat cards.
 *
 * This function has 6 code branches (3 StatCardType × increase/decrease) plus null handling.
 * Bugs here = user sees wrong trend information on Home screen.
 */
class BuildTrendUiStateTest {

    // ===== Null/invalid input =====

    @Test
    fun buildTrendUiState_nonNumericTrend_returnsNull() {
        // Given: a non-numeric string
        val trend = "abc"

        // When
        val result = buildTrendUiState(trend, StatCardType.TOTAL_FOCUS_TIME)

        // Then: returns null (can't parse)
        assertNull(result)
    }

    // ===== TOTAL_FOCUS_TIME =====

    @Test
    fun buildTrendUiState_focusTimePositive_returnsIncreaseWithGreenColor() {
        // Given: positive seconds difference
        val trend = "120" // 2 phút

        // When
        val result = buildTrendUiState(trend, StatCardType.TOTAL_FOCUS_TIME, isCompact = false)

        // Then
        assertEquals(AuraColors.IncreaseMode, result!!.color)
        assertTrue(result.text.contains("↑"))
        assertTrue(result.text.contains("2 phút"))
        assertTrue(result.text.contains("so với hôm qua"))
    }

    @Test
    fun buildTrendUiState_focusTimeNegative_returnsDecreaseWithRedColor() {
        // Given: negative seconds difference
        val trend = "-300" // -5 phút

        // When
        val result = buildTrendUiState(trend, StatCardType.TOTAL_FOCUS_TIME, isCompact = false)

        // Then
        assertEquals(AuraColors.DecreaseMode, result!!.color)
        assertTrue(result.text.contains("↓"))
        assertTrue(result.text.contains("5 phút"))
    }

    // ===== BEST_STREAK — completely different branch =====

    @Test
    fun buildTrendUiState_bestStreak_returnsRecordTextWithGoldColor() {
        // Given
        val trend = "15"

        // When
        val result = buildTrendUiState(trend, StatCardType.BEST_STREAK, isCompact = false)

        // Then: different format from the other two types
        assertEquals(AuraColors.BestStreakDay, result!!.color)
        assertTrue(result.text.contains("Kỷ lục: 15 ngày"))
    }

    // ===== Compact mode =====

    @Test
    fun buildTrendUiState_compactMode_omitsSoVoiHomQua() {
        // Given: compact = true
        val trend = "60" // 1 phút

        // When
        val result = buildTrendUiState(trend, StatCardType.TOTAL_FOCUS_TIME, isCompact = true)

        // Then: should NOT contain the "so với hôm qua" suffix
        assertTrue(result!!.text.contains("↑"))
        assertTrue(result.text.contains("1 phút"))
        assertTrue(!result.text.contains("so với hôm qua"))
    }
}
