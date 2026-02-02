package com.sdevprem.runtrack.shared.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sdevprem.runtrack.shared.createDataStore
import com.sdevprem.runtrack.shared.data.tracking.location.DefaultLocationTrackingManager
import getDatabaseBuilder
import getRoomDatabase
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module


fun initKoinIos() {
    startKoin {
        modules(
            module {
                single<DataStore<Preferences>> { createDataStore() }
                single { getRoomDatabase(getDatabaseBuilder()) }
                single { DefaultLocationTrackingManager() }
            },
            AppModule().module,
            )
    }
}