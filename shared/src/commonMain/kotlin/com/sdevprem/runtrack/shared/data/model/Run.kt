package com.sdevprem.runtrack.shared.data.model

import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sdevprem.runtrack.shared.utils.now
import kotlinx.datetime.LocalDate

@Entity(tableName = "running_table")
data class Run(
    var img: ImageBitmap,
    var timestamp: LocalDate = LocalDate.now(),
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var durationInMillis: Long = 0L,
    var caloriesBurned: Int = 0,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
