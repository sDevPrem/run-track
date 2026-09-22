package com.sdevprem.runtrack.shared.data

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import okio.Path.Companion.toPath

internal const val userDataStoreName = "user_preferences.preferences_pb"

fun createUserDatastore(
    producePath: () -> String,
    coroutineScope: CoroutineScope? = null,
    migrations: List<DataMigration<Preferences>> = listOf()
): DataStore<Preferences> {
    return if (coroutineScope == null) {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { producePath().toPath() },
            migrations = migrations
        )
    } else {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { producePath().toPath() },
            scope = coroutineScope,
            migrations = migrations
        )
    }
}