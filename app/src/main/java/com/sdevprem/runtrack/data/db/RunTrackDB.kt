package com.sdevprem.runtrack.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sdevprem.runtrack.data.db.dao.RunDao
import com.sdevprem.runtrack.data.db.mapper.DBConverters
import com.sdevprem.runtrack.data.model.Run

@Database(
    entities = [Run::class],
    version = 1,
)

@TypeConverters(DBConverters::class)
abstract class RunTrackDB : RoomDatabase() {
    companion object {
        const val RUN_TRACK_DB_NAME = "run_track_db"
    }

    abstract fun getRunDao(): RunDao

}