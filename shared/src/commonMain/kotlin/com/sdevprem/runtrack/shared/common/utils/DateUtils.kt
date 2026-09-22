package com.sdevprem.runtrack.shared.common.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

object DateUtils {

    private val formatMMMDDYYYY by lazy {
        LocalDateTime.Format {
            monthName(MonthNames.ENGLISH_FULL) // "MMMM"
            char(' ')
            day(padding = Padding.ZERO) // "dd"
            char(',')
            char(' ')
            year() // "yyyy"
        }
    }

    private val formatDD by lazy {
        LocalDate.Format {
            day(padding = Padding.ZERO)
        }
    }

    private val formatDay by lazy {
        LocalDate.Format {
            dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
        }
    }

    private val formatMMMDD by lazy {
        LocalDate.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED) // "MMM"
            char(' ')
            day(padding = Padding.ZERO) // "dd"
        }
    }

    fun getFormattedStopwatchTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600

        return buildString {
            if (hours < 10) append('0')
            append(hours)
            append(':')
            if (minutes < 10) append('0')
            append(minutes)
            append(':')
            if (seconds < 10) append('0')
            append(seconds)
        }
    }

    fun formatDateMMMDDYYYY(date: LocalDateTime): String {
        return formatMMMDDYYYY.format(date)
    }

    fun formatDD(date: LocalDate): String {
        return formatDD.format(date)
    }

    fun formatDay(date: LocalDate): String {
        return formatDay.format(date)
    }

    fun formatMMMDD(date: LocalDate): String {
        return formatMMMDD.format(date)
    }

}
