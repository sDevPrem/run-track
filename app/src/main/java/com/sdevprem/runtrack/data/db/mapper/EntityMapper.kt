package com.sdevprem.runtrack.data.db.mapper

import com.sdevprem.runtrack.data.model.Run

fun Run.toEntity(): com.sdevprem.runtrack.shared.data.model.Run {
    return com.sdevprem.runtrack.shared.data.model.Run(
        id = this.id,
        img = DBConverters.fromBitmapToByteArray(this.img),
        timestamp = this.timestamp.time,
        avgSpeedInKMH = this.avgSpeedInKMH,
        distanceInMeters = this.distanceInMeters,
        durationInMillis = this.durationInMillis,
        caloriesBurned = this.caloriesBurned,
    )
}