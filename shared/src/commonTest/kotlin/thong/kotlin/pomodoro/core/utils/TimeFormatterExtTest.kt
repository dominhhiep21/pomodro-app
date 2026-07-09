package thong.kotlin.pomodoro.core.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests for toMillisFromDateTimeText() and toDateTimeText() — the custom date parsing/formatting
 * functions used throughout data layer mapping (LearningSessionRecord, SessionTask).
 *
 * These functions use regex parsing + ISO fallback and are called 15+ times in production code.
 * Round-trip correctness is critical for data integrity.
 */
class TimeFormatterExtTest {

    // ===== toMillisFromDateTimeText — Regex path (DD-MM-YYYY HH:mm:ss) =====

    @Test
    fun toMillisFromDateTimeText_validLocalFormat_parsesWithoutCrash() {
        // Given: a string in DD-MM-YYYY HH:mm:ss format
        val input = "15-07-2023 14:30:00"

        // When: parsed to millis
        val result = input.toMillisFromDateTimeText()

        // Then: result is a positive epoch millis (we can't assert exact value due to timezone)
        assert(result > 0) { "Expected positive millis but got $result" }
    }

    @Test
    fun toMillisFromDateTimeText_isoFormat_fallsBackToInstantParse() {
        // Given: an ISO 8601 string that does NOT match the DD-MM-YYYY regex
        val input = "2023-07-15T14:30:00Z"

        // When: parsed to millis
        val result = input.toMillisFromDateTimeText()

        // Then: returns the same value as toMillis() which also uses Instant.parse
        // This verifies the ISO fallback path works correctly
        val expected = input.toMillis()
        assertEquals(expected, result)
    }

    // ===== Round-trip: toDateTimeText() → toMillisFromDateTimeText() =====

    @Test
    fun toDateTimeText_roundTrip_preservesValueToSecondPrecision() {
        // Given: a known epoch millis (truncated to whole seconds)
        val originalMillis = 1689400000000L // some timestamp

        // When: convert to text and back
        val text = originalMillis.toDateTimeText()
        val roundTripped = text.toMillisFromDateTimeText()

        // Then: round-trip preserves value (may differ by timezone offset but should be consistent)
        // The key invariant: converting the SAME millis to text and back gives the SAME millis
        assertEquals(originalMillis, roundTripped)
    }

    // ===== toMillisFromDateTimeTextOrNull — Error handling =====

    @Test
    fun toMillisFromDateTimeTextOrNull_invalidInput_returnsNull() {
        // Given: garbage input that matches neither regex nor ISO format
        val input = "not-a-valid-date-at-all"

        // When
        val result = input.toMillisFromDateTimeTextOrNull()

        // Then: returns null instead of crashing
        assertNull(result)
    }

    @Test
    fun toMillisFromDateTimeTextOrNull_nullInput_returnsNull() {
        // Given
        val input: String? = null

        // When
        val result = input.toMillisFromDateTimeTextOrNull()

        // Then
        assertNull(result)
    }
}
