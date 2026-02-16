package com.sdevprem.runtrack.shared.di

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sdevprem.runtrack.shared.background.DefaultBackgroundTrackingManager
import com.sdevprem.runtrack.shared.background.notification.TrackingNotificationHelper
import com.sdevprem.runtrack.shared.createDataStore
import com.sdevprem.runtrack.shared.data.db.RunTrackDB
import com.sdevprem.runtrack.shared.data.migration.DataStoreMigration
import com.sdevprem.runtrack.shared.data.tracking.location.DefaultLocationTrackingManager
import com.sdevprem.runtrack.shared.data.tracking.location.LocationUtils
import com.sdevprem.runtrack.shared.data.utils.AndroidLocalFileProcessor
import com.sdevprem.runtrack.shared.data.utils.LocalFileProcessor
import com.sdevprem.runtrack.shared.di.AppModule.Companion.USER_PREFERENCES_FILE_NAME
import com.sdevprem.runtrack.shared.domain.tracking.background.BackgroundTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.shared.ui.MainActivity
import com.sdevprem.runtrack.shared.ui.nav.Destination
import getDatabaseBuilder
import getRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.sdevprem.runtrack.shared")
object PlatformModule {

    @Single
    @Named("OldPreferenceDataStore")
    fun providesOldPreferenceDataStore(
        context: Context,
        @Named("ApplicationScope") scope: CoroutineScope,
        dispatchers: CoroutineDispatchers
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_FILE_NAME) },
            scope = scope.plus(dispatchers.io + SupervisorJob())
        )

    @Single
    fun providesNewPreferenceDataStore(
        context: Context,
        @Named("ApplicationScope") scope: CoroutineScope,
        dispatchers: CoroutineDispatchers,
        @Named("OldPreferenceDataStore") oldDataStore: DataStore<Preferences>
    ): DataStore<Preferences> {
        return createDataStore(
            context,
            scope.plus(dispatchers.io + SupervisorJob()),
            listOf(DataStoreMigration(oldDataStore)),
        )

    }

    @Single
    fun provideFusedLocationProviderClient(
        context: Context
    ) = LocationServices
        .getFusedLocationProviderClient(context)

    @Single
    fun provideSharedRunningDB(
        context: Context
    ): RunTrackDB = getRoomDatabase(
        getDatabaseBuilder(context)
    )

    @Single
    fun provideLocationTrackingManager(
        context: Context,
        fusedLocationProviderClient: FusedLocationProviderClient,
    ): LocationTrackingManager {
        return DefaultLocationTrackingManager(
            fusedLocationProviderClient = fusedLocationProviderClient,
            context = context,
            locationRequest = LocationUtils.locationRequestBuilder.build()
        )
    }

    @Single
    fun provideBackgroundTrackingManager(
        context: Context,
    ): BackgroundTrackingManager = DefaultBackgroundTrackingManager(context)

    @Single
    fun providesNotificationHelper(
        context: Context,
    ) = TrackingNotificationHelper(
        context,
        Intent(
            Intent.ACTION_VIEW,
            Destination.CurrentRun.currentRunUriPattern.toUri(),
            context,
            MainActivity::class.java
        )
    )

    @Single
    fun providesLocalFileProcessor(
        context: Context
    ): LocalFileProcessor {
        return AndroidLocalFileProcessor(context)
    }

}