package com.sdevprem.runtrack.data.migration

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class DataStoreMigration(
    private val oldPrefs: androidx.datastore.core.DataStore<Preferences>
) : DataMigration<Preferences> {
    override suspend fun shouldMigrate(currentData: Preferences) =
        oldPrefs.data.first().asMap().isEmpty().not()

    override suspend fun migrate(currentData: Preferences): Preferences {
        val oldData = oldPrefs.data.first().asMap()
        val currentMutablePrefs = currentData.toMutablePreferences()

        mapOldToNewPrefs(oldData, currentMutablePrefs)
        return currentMutablePrefs.toPreferences()
    }

    override suspend fun cleanUp() {
        oldPrefs.edit { it.clear() }
    }

    private fun mapOldToNewPrefs(
        oldData: Map<Preferences.Key<*>, Any>,
        currentMutablePrefs: MutablePreferences
    ) {
        oldData.forEach { (key, value) ->
            when (value) {
                is String ->
                    currentMutablePrefs[stringPreferencesKey(key.name)] = value

                is Float ->
                    currentMutablePrefs[floatPreferencesKey(key.name)] = value
            }
        }
    }
}