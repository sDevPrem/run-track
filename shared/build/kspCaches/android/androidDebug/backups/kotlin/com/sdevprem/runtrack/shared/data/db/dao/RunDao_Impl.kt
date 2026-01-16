package com.sdevprem.runtrack.shared.`data`.db.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.sdevprem.runtrack.shared.`data`.model.Run
import javax.`annotation`.processing.Generated
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class RunDao_Impl(
  __db: RoomDatabase,
) : RunDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRun: EntityInsertAdapter<Run>

  private val __deleteAdapterOfRun: EntityDeleteOrUpdateAdapter<Run>
  init {
    this.__db = __db
    this.__insertAdapterOfRun = object : EntityInsertAdapter<Run>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `running_table` (`img`,`timestamp`,`avgSpeedInKMH`,`distanceInMeters`,`durationInMillis`,`caloriesBurned`,`id`) VALUES (?,?,?,?,?,?,nullif(?, 0))"

      protected override fun bind(statement: SQLiteStatement, entity: Run) {
        statement.bindBlob(1, entity.img)
        statement.bindLong(2, entity.timestamp)
        statement.bindDouble(3, entity.avgSpeedInKMH.toDouble())
        statement.bindLong(4, entity.distanceInMeters.toLong())
        statement.bindLong(5, entity.durationInMillis)
        statement.bindLong(6, entity.caloriesBurned.toLong())
        statement.bindLong(7, entity.id.toLong())
      }
    }
    this.__deleteAdapterOfRun = object : EntityDeleteOrUpdateAdapter<Run>() {
      protected override fun createQuery(): String = "DELETE FROM `running_table` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Run) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
  }

  public override suspend fun insertRun(run: Run): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfRun.insert(_connection, run)
  }

  public override suspend fun deleteRun(run: Run): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfRun.handle(_connection, run)
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
