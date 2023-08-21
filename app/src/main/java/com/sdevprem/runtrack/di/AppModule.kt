package com.sdevprem.runtrack.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sdevprem.runtrack.core.data.db.RunTrackDB
import com.sdevprem.runtrack.core.data.db.RunTrackDB.Companion.RUN_TRACK_DB_NAME
import com.sdevprem.runtrack.core.tracking.location.DefaultLocationTrackingManager
import com.sdevprem.runtrack.core.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.core.tracking.location.LocationUtils
import com.sdevprem.runtrack.core.tracking.notification.DefaultNotificationHelper
import com.sdevprem.runtrack.core.tracking.notification.NotificationHelper
import com.sdevprem.runtrack.core.tracking.service.DefaultTrackingServiceManager
import com.sdevprem.runtrack.core.tracking.service.TrackingServiceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object {

        private const val USER_PREFERENCES_FILE_NAME = "user_preferences"

        @Singleton
        @Provides
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ) = LocationServices
            .getFusedLocationProviderClient(context)

        @Provides
        @Singleton
        fun provideRunningDB(
            @ApplicationContext context: Context
        ): RunTrackDB = Room.databaseBuilder(
            context,
            RunTrackDB::class.java,
            RUN_TRACK_DB_NAME
        ).build()

        @Singleton
        @Provides
        fun provideRunDao(db: RunTrackDB) = db.getRunDao()

        @Provides
        @Singleton
        fun providesPreferenceDataStore(
            @ApplicationContext context: Context,
            @ApplicationScope scope: CoroutineScope,
            @IoDispatcher ioDispatcher: CoroutineDispatcher
        ): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { emptyPreferences() }
                ),
                produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_FILE_NAME) },
                scope = scope.plus(ioDispatcher + SupervisorJob())
            )

        @Singleton
        @Provides
        fun provideLocationTrackingManager(
            @ApplicationContext context: Context,
            fusedLocationProviderClient: FusedLocationProviderClient,
        ): LocationTrackingManager {
            return DefaultLocationTrackingManager(
                fusedLocationProviderClient = fusedLocationProviderClient,
                context = context,
                locationRequest = LocationUtils.locationRequestBuilder.build()
            )
        }

    }

    @Binds
    @Singleton
    abstract fun provideTrackingServiceManager(
        trackingServiceManager: DefaultTrackingServiceManager
    ): TrackingServiceManager

    @Binds
    @Singleton
    abstract fun provideNotificationHelper(
        notificationHelper: DefaultNotificationHelper
    ): NotificationHelper


}