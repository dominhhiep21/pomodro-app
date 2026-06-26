package thong.kotlin.pomodoro.features.streak.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal actual fun getCurrentDate(): String =
    LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
