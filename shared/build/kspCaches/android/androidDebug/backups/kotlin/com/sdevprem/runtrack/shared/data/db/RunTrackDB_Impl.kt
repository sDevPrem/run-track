package com.sdevprem.runtrack.shared.`data`.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.sdevprem.runtrack.shared.`data`.db.dao.RunDao
import com.sdevprem.runtrack.shared.`data`.db.dao.RunDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class RunTrackDB_Impl : RunTrackDB() {
  private val _runDao: Lazy<RunDao> = lazy {
    RunDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "c071d4aa2c7f2604589e43cc09d62520", "343ce172948afa96fd8dc5f8c3846859") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `running_table` (`img` BLOB NOT NULL, `timestamp` INTEGER NOT NULL, `avgSpeedInKMH` REAL NOT NULL, `distanceInMeters` INTEGER NOT NULL, `durationInMillis` INTEGER NOT NULL, `caloriesBurned` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c071d4aa2c7f2604589e43cc09d62520')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `running_table`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsRunningTable: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRunningTable.put("img", TableInfo.Column("img", "BLOB", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRunningTable.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRunningTable.put("avgSpeedInKMH", TableInfo.Column("avgSpeedInKMH", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRunningTable.put("distanceInMeters", TableInfo.Column("distanceInMeters", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRunningTable.put("durationInMillis", TableInfo.Column("durationInMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRunningTable.put("caloriesBurned", TableInfo.Column("caloriesBurned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRunningTable.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRunningTable: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRunningTable: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRunningTable: TableInfo = TableInfo("running_table", _columnsRunningTable, _foreignKeysRunningTable, _indicesRunningTable)
        val _existingRunningTable: TableInfo = read(connection, "running_table")
        if (!_infoRunningTable.equals(_existingRunningTable)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |running_table(com.sdevprem.runtrack.shared.data.model.Run).
              | Expected:
              |""".trimMargin() + _infoRunningTable + """
              |
              | Found:
              |""".trimMargin() + _existingRunningTable)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "running_table")
  }

  public override fun clearAllTables() {
    super.performClear(false, "running_table")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(RunDao::class, RunDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun getRunDao(): RunDao = _runDao.value
}
