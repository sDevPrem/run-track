package com.sdevprem.runtrack.shared.data.model

//import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
//@TypeConverters(DBConverters::class)
data class Run(
    var img: ByteArray,
    var timestamp: Long,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var durationInMillis: Long = 0L,
    var caloriesBurned: Int = 0,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
