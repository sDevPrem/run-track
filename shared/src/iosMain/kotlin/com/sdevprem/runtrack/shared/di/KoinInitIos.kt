package com.sdevprem.runtrack.shared.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sdevprem.runtrack.shared.background.DefaultBackgroundTrackingManager
import com.sdevprem.runtrack.shared.createDataStore
import com.sdevprem.runtrack.shared.data.tracking.location.DefaultLocationTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.background.BackgroundTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.location.LocationTrackingManager
import getDatabaseBuilder
import getRoomDatabase
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module
import platform.CoreLocation.CLLocationManager


fun initKoinIos() {
    startKoin {
        modules(
            module {
                single<DataStore<Preferences>> { createDataStore() }
                single { getRoomDatabase(getDatabaseBuilder()) }
                single { CLLocationManager() }
                single<LocationTrackingManager> { DefaultLocationTrackingManager(get()) }
                single<BackgroundTrackingManager> { DefaultBackgroundTrackingManager() }
            },
            AppModule().module,
        )
    }
}