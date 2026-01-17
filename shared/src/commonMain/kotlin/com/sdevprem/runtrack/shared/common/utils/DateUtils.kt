package com.sdevprem.runtrack.shared.common.utils

import kotlinx.datetime.LocalDateTime
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

}
