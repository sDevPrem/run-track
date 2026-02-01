package com.sdevprem.runtrack.shared.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sdevprem.runtrack.shared.createDataStore
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class PlatformModule {

    @Single
    fun providesPreferenceDataStore(): DataStore<Preferences> = createDataStore()
}

