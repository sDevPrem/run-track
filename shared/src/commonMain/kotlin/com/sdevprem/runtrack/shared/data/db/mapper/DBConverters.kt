package com.sdevprem.runtrack.shared.data.db.mapper

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object DBConverters {
    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun fromTimeInMillisToDate(timeInMillis: Long): LocalDate {
        return Instant.Companion.fromEpochSeconds(timeInMillis)
            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()).date
    }

    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun fromDateToTimeInMillis(date: LocalDateTime?): Long? {
        return date
            ?.toInstant(TimeZone.Companion.currentSystemDefault())
            ?.toEpochMilliseconds()
    }

}