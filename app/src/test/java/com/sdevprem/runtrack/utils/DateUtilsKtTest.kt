package com.sdevprem.runtrack.utils

import com.sdevprem.runtrack.common.extension.setDateToWeekFirstDay
import com.sdevprem.runtrack.common.extension.setDateToWeekLastDay
import com.sdevprem.runtrack.common.extension.setMaximumTime
import com.sdevprem.runtrack.common.extension.setMinimumTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

internal class DateUtilsKtTest {

    lateinit var calendar: Calendar

    @Before
    fun setUp() {
        //testing for Indian Standard Time
        //and for date 09-08-2023
        calendar = Calendar.getInstance(TimeZone.getTimeZone("IST")).apply {
            set(Calendar.DATE, 9)
            set(Calendar.MONTH, Calendar.AUGUST)
            set(Calendar.YEAR, 2023)
        }
    }

    @Test
    fun testSetDateToWeekFirstDay_expected_WeekFirstDateWithMinTime() {
        calendar.setDateToWeekFirstDay()
        assertEquals(calendar.get(Calendar.DATE), 6)
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 0)
        assertEquals(calendar.get(Calendar.MINUTE), 0)
        assertEquals(calendar.get(Calendar.SECOND), 0)
    }

    @Test
    fun testSetDateToWeekLastDay_expected_WeekLastDateWithMaxTime() {
        calendar.setDateToWeekLastDay()
        assertEquals(calendar.get(Calendar.DATE), 12)
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 23)
        assertEquals(calendar.get(Calendar.MINUTE), 59)
        assertEquals(calendar.get(Calendar.SECOND), 59)
    }

    @Test
    fun testSetMinimumTime_expected_MinimumTimeOfDay() {
        calendar.setMinimumTime()
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 0)
        assertEquals(calendar.get(Calendar.MINUTE), 0)
        assertEquals(calendar.get(Calendar.SECOND), 0)
    }

    @Test
    fun testSetMaximumTime_expected_MaximumTimeOfDay() {
        calendar.setMaximumTime()
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 23)
        assertEquals(calendar.get(Calendar.MINUTE), 59)
        assertEquals(calendar.get(Calendar.SECOND), 59)
    }
}