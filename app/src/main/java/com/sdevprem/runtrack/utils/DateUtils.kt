package com.sdevprem.runtrack.utils

import java.util.Calendar

fun Calendar.setDateToWeekFirstDay() = apply {
    val firstDay = getActualMinimum(Calendar.DAY_OF_WEEK)
    set(Calendar.DAY_OF_WEEK, firstDay)
    setMinimumTime()
}

fun Calendar.setDateToWeekLastDay() = apply {
    val lastDay = getActualMaximum(Calendar.DAY_OF_WEEK)
    set(Calendar.DAY_OF_WEEK, lastDay)
    setMaximumTime()
}

fun Calendar.setMinimumTime() = apply {
    set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
    set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
    set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
    set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
}

fun Calendar.setMaximumTime() = apply {
    set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
    set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
    set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
    set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
}
