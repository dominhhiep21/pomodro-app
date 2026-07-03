package thong.kotlin.pomodoro.core.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual fun getCurrentDateString(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    formatter.locale = NSLocale.currentLocale
    return formatter.stringFromDate(NSDate())
}

actual fun getCurrentDateTimeString(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
    formatter.locale = NSLocale.currentLocale
    return formatter.stringFromDate(NSDate())
}
