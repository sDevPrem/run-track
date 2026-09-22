package com.sdevprem.runtrack.shared.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sdevprem.runtrack.shared.createDataStore
import com.sdevprem.runtrack.shared.data.db.RunTrackDB
import getDatabaseBuilder
import getRoomDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.sdevprem.runtrack.shared")
class PlatformModule {

    @Single
    fun providesPreferenceDataStore(): DataStore<Preferences> = createDataStore()

    @Single
    fun provideSharedRunningDB(
    ): RunTrackDB = getRoomDatabase(
        getDatabaseBuilder()
    )
}

