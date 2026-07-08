package thong.kotlin.pomodoro.core.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class TimeFormatterTest {

    // ===== Long.formatToMmSs =====

    @Test
    fun formatToMmSs_zero_returns0000() {
        val actual = 0L.formatToMmSs()
        assertEquals("00:00", actual)
    }

    @Test
    fun formatToMmSs_65seconds_returns0105() {
        val actual = 65L.formatToMmSs()
        assertEquals("01:05", actual)
    }

    @Test
    fun formatToMmSs_3600seconds_returns6000() {
        val actual = 3600L.formatToMmSs()
        assertEquals("60:00", actual)
    }

    // ===== Int.formatToMmSs =====

    @Test
    fun formatToMmSsInt_90seconds_returns0130() {
        val actual = 90.formatToMmSs()
        assertEquals("01:30", actual)
    }

    // ===== Int.formatSeconds =====

    @Test
    fun formatSeconds_30seconds_returnsGiay() {
        val actual = 30.formatSeconds()
        assertEquals("30 giây", actual)
    }

    @Test
    fun formatSeconds_120seconds_returns2Phut() {
        val actual = 120.formatSeconds()
        assertEquals("2 phút", actual)
    }

    @Test
    fun formatSeconds_3600seconds_returns1Gio() {
        val actual = 3600.formatSeconds()
        assertEquals("1 giờ", actual)
    }

    @Test
    fun formatSeconds_3720seconds_returns1Gio2Phut() {
        val actual = 3720.formatSeconds()
        assertEquals("1 giờ 2 phút", actual)
    }

    // ===== secondsToMinutesText =====

    @Test
    fun secondsToMinutesText_90seconds_returns1m30s() {
        val actual = secondsToMinutesText(90)
        assertEquals("1m 30s", actual)
    }

    @Test
    fun secondsToMinutesText_0seconds_returns0m0s() {
        val actual = secondsToMinutesText(0)
        assertEquals("0m 0s", actual)
    }

    // ===== secondsToHourMinuteText =====

    @Test
    fun secondsToHourMinuteText_3660_returns1h1m() {
        val actual = secondsToHourMinuteText(3660L)
        assertEquals("1h 1m", actual)
    }

    @Test
    fun secondsToHourMinuteText_1800_returns30m() {
        val actual = secondsToHourMinuteText(1800L)
        assertEquals("30m", actual)
    }

    @Test
    fun secondsToHourMinuteText_0_returns0m() {
        val actual = secondsToHourMinuteText(0L)
        assertEquals("0m", actual)
    }
}
