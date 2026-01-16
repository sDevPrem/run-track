import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sdevprem.runtrack.shared.data.db.RunTrackDB

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<RunTrackDB> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("my_room.db")
    return Room.databaseBuilder<RunTrackDB>(
        context = appContext,
        name = dbFile.absolutePath
    )
}