package com.sdevprem.runtrack.shared.common.extension

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun LocalDateTime.toWeekFirstDay(): LocalDateTime {
    // Treat Sunday as start of week
    // isoDayNumber: Mon=1, ... Sat=6, Sun=7
    // % 7 mapping: Sun=0, Mon=1, ... Sat=6
    val daysToSubtract = date.dayOfWeek.isoDayNumber % 7
    val firstDay = date.minus(DatePeriod(days = daysToSubtract))

    return firstDay.atTime(0, 0, 0, 0)
}

fun LocalDateTime.toWeekLastDay(): LocalDateTime {
    // Treat Saturday as end of week
    val currentDayIndex = date.dayOfWeek.isoDayNumber % 7 // Sun=0 ... Sat=6
    val daysToAdd = 6 - currentDayIndex
    val lastDay = date.plus(DatePeriod(days = daysToAdd))

    return lastDay.atTime(23, 59, 59, 999_999_999)
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.Companion.currentSystemDefault())
}

fun LocalDate.Companion.now(): LocalDate {
    return LocalDateTime.Companion.now().date
}

fun LocalTime.Companion.now(): LocalTime {
    return LocalDateTime.now().time
}