package thong.kotlin.pomodoro.features.streak.data

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

internal actual fun getCurrentDate(): String {
    val formatter = NSDateFormatter().apply { dateFormat = "yyyy-MM-dd" }
    return formatter.stringFromDate(NSDate())
}
