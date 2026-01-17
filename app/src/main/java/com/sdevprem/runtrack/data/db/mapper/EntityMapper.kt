package com.sdevprem.runtrack.data.db.mapper

import com.sdevprem.runtrack.data.model.Run
import com.sdevprem.runtrack.shared.data.db.mapper.DBConverters.fromDateToTimeInMillis
import kotlinx.datetime.LocalDateTime
import java.util.Date

fun Run.toEntity(): com.sdevprem.runtrack.shared.data.model.Run {
    return com.sdevprem.runtrack.shared.data.model.Run(
        id = this.id,
        img = DBConverters.fromBitmapToByteArray(this.img),
        timestamp = com.sdevprem.runtrack.shared.data.db.mapper.DBConverters.fromTimeInMillisToLocalDate(
            this.timestamp.time
        ),
        avgSpeedInKMH = this.avgSpeedInKMH,
        distanceInMeters = this.distanceInMeters,
        durationInMillis = this.durationInMillis,
        caloriesBurned = this.caloriesBurned,
    )
}

fun com.sdevprem.runtrack.shared.data.model.Run.toDataModel(): Run {
    return Run(
        id = this.id,
        img = DBConverters.fromByteArrayToBitmap(this.img),
        timestamp = this.timestamp.toDate(),
        avgSpeedInKMH = this.avgSpeedInKMH,
        distanceInMeters = this.distanceInMeters,
        durationInMillis = this.durationInMillis,
        caloriesBurned = this.caloriesBurned,
    )
}

fun Date.toDateTime() = com.sdevprem.runtrack.shared.data.db.mapper.DBConverters
    .fromTimeInMillisToLocalDate(this.time)

fun LocalDateTime.toDate() = Date(fromDateToTimeInMillis(this) ?: System.currentTimeMillis())