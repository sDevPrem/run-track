package com.sdevprem.runtrack.shared

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import com.sdevprem.runtrack.shared.data.createUserDatastore
import com.sdevprem.runtrack.shared.data.userDataStoreName
import kotlinx.coroutines.CoroutineScope

fun createDataStore(
    context: Context,
    coroutineScope: CoroutineScope,
    migrations: List<DataMigration<Preferences>> = listOf(),
    name: String = userDataStoreName
) = createUserDatastore(
    producePath = { context.filesDir.resolve(name).absolutePath },
    coroutineScope = coroutineScope,
    migrations
)