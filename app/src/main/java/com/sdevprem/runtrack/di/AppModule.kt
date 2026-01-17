package com.sdevprem.runtrack.di

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
import com.sdevprem.runtrack.data.migration.DataStoreMigration
import com.sdevprem.runtrack.data.migration.OldPrefs
import com.sdevprem.runtrack.data.tracking.location.LocationUtils
import com.sdevprem.runtrack.shared.background.DefaultBackgroundTrackingManager
import com.sdevprem.runtrack.shared.background.notification.TrackingNotificationHelper
import com.sdevprem.runtrack.shared.createDataStore
import com.sdevprem.runtrack.shared.data.db.RunTrackDB
import com.sdevprem.runtrack.shared.data.db.dao.RunDao
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.data.repository.UserRepository
import com.sdevprem.runtrack.shared.data.tracking.location.DefaultLocationTrackingManager
import com.sdevprem.runtrack.shared.data.tracking.timer.DefaultTimeTracker
import com.sdevprem.runtrack.shared.domain.tracking.background.BackgroundTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.timer.TimeTracker
import com.sdevprem.runtrack.ui.MainActivity
import com.sdevprem.runtrack.ui.nav.Destination
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import getDatabaseBuilder
import getRoomDatabase
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
        fun provideSharedRunningDB(
            @ApplicationContext context: Context
        ): RunTrackDB = getRoomDatabase(
            getDatabaseBuilder(context)
        )

        @Singleton
        @Provides
        fun provideSharedRunDao(db: RunTrackDB) =
            db.getRunDao()

        @Singleton
        @Provides
        fun provideAppRepository(
            runDao: RunDao
        ): AppRepository = AppRepository(runDao = runDao)

        @Provides
        @Singleton
        @OldPrefs
        fun providesOldPreferenceDataStore(
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

        @Provides
        @Singleton
        fun providesPreferenceDataStore(
            @ApplicationContext context: Context,
            @ApplicationScope scope: CoroutineScope,
            @IoDispatcher ioDispatcher: CoroutineDispatcher,
            @OldPrefs oldPrefs: DataStore<Preferences>,
        ): DataStore<Preferences> = createDataStore(
            context,
            scope.plus(ioDispatcher + SupervisorJob()),
            listOf(DataStoreMigration(oldPrefs))
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

        @Singleton
        @Provides
        fun provideUserRepository(
            datastore: DataStore<Preferences>
        ) = UserRepository(datastore)

        @Singleton
        @Provides
        fun providesTimeTracker(
            @ApplicationScope scope: CoroutineScope,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): TimeTracker = DefaultTimeTracker(scope, dispatcher)

        @Singleton
        @Provides
        fun provideBackgroundTrackingManager(
            @ApplicationContext context: Context,
        ): BackgroundTrackingManager = DefaultBackgroundTrackingManager(context)

        @Singleton
        @Provides
        fun providesNotificationHelper(
            @ApplicationContext context: Context,
        ) = TrackingNotificationHelper(
            context,
            Intent(
                Intent.ACTION_VIEW,
                Destination.CurrentRun.currentRunUriPattern.toUri(),
                context,
                MainActivity::class.java
            )
        )
    }

}

