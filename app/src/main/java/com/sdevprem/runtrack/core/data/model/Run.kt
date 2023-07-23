package com.sdevprem.runtrack.core.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "running_table")
data class Run(
    var img: Bitmap,
    var timestamp: Date = Date(),
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var durationInMillis: Long = 0L,
    var caloriesBurned: Int = 0,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
