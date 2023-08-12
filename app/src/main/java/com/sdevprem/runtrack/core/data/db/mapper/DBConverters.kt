package com.sdevprem.runtrack.core.data.db.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.util.Date

object DBConverters {
    @TypeConverter
    fun fromByteArrayToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromBitmapToByteArray(bitmap: Bitmap): ByteArray {
        return ByteArrayOutputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            return@use it.toByteArray()
        }
    }

    @TypeConverter
    fun fromTimeInMillisToDate(timeInMillis: Long): Date {
        return Date(timeInMillis)
    }

    @TypeConverter
    fun fromDateToTimeInMillis(date: Date?): Long? {
        return date?.time
    }

}
