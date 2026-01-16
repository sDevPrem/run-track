import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sdevprem.runtrack.shared.data.db.RunTrackDB
import com.sdevprem.runtrack.shared.data.db.RunTrackDB.Companion.RUN_TRACK_DB_NAME

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<RunTrackDB> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(RUN_TRACK_DB_NAME)
    return Room.databaseBuilder<RunTrackDB>(
        context = appContext,
        name = dbFile.absolutePath
    )
}