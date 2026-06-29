package thong.kotlin.pomodoro.core.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Định dạng số giây (Long) thành định dạng chuỗi hiển thị MM:SS
 * Ví dụ: 65L -> "01:05"
 */
fun Long.formatToMmSs(): String {
    val minutes = this / 60
    val seconds = this % 60

    val minString = minutes.toString().padStart(2, '0')
    val secString = seconds.toString().padStart(2, '0')

    return "$minString:$secString"
}

fun Int.formatToMmSs(): String {
    val minutes = this / 60
    val seconds = this % 60

    val minString = minutes.toString().padStart(2, '0')
    val secString = seconds.toString().padStart(2, '0')

    return "$minString:$secString"
}

fun Long.toIsoString(): String {
    return Instant.fromEpochMilliseconds(this).toString()
}

fun Long?.toIsoStringOrNull(): String? {
    return this?.let { Instant.fromEpochMilliseconds(it).toString() }
}

fun String.toMillis(): Long {
    return Instant.parse(this).toEpochMilliseconds()
}

fun String?.toMillisOrNull(): Long? {
    return this?.let { Instant.parse(it).toEpochMilliseconds() }
}

fun Long.toDateTimeText(): String {
    val dateTime = Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    return "${dateTime.day.pad2()}-" +
            "${dateTime.month.number.pad2()}-" +
            "${dateTime.year} " +
            "${dateTime.hour.pad2()}:" +
            "${dateTime.minute.pad2()}:" +
            dateTime.second.pad2()
}

fun Long?.toDateTimeTextOrNull(): String? {
    return this?.toDateTimeText()
}

fun String.toMillisFromDateTimeText(): Long {
    val value = trim()

    val localDateTimeRegex =
        Regex("""^(\d{2})-(\d{2})-(\d{4}) (\d{2}):(\d{2}):(\d{2})$""")

    val match = localDateTimeRegex.matchEntire(value)

    if (match != null) {
        val day = match.groupValues[1].toInt()
        val month = match.groupValues[2].toInt()
        val year = match.groupValues[3].toInt()
        val hour = match.groupValues[4].toInt()
        val minute = match.groupValues[5].toInt()
        val second = match.groupValues[6].toInt()

        return LocalDateTime(
            year = year,
            month = month,
            day = day,
            hour = hour,
            minute = minute,
            second = second,
            nanosecond = 0
        )
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    }

    return Instant.parse(value).toEpochMilliseconds()
}

fun String?.toMillisFromDateTimeTextOrNull(): Long? {
    return this?.let { value ->
        runCatching {
            value.toMillisFromDateTimeText()
        }.getOrNull()
    }
}

private fun Int.pad2(): String {
    return toString().padStart(2, '0')
}

fun secondsToMinutesText(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "${minutes}m ${remainingSeconds}s"
}

fun secondsToHourMinuteText(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60

    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}