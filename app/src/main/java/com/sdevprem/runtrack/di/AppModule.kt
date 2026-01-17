package com.sdevprem.runtrack.di

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sdevprem.runtrack.background.tracking.service.DefaultBackgroundTrackingManager
import com.sdevprem.runtrack.data.tracking.location.DefaultLocationTrackingManager
import com.sdevprem.runtrack.data.tracking.location.LocationUtils
import com.sdevprem.runtrack.data.tracking.timer.DefaultTimeTracker
import com.sdevprem.runtrack.domain.tracking.background.BackgroundTrackingManager
import com.sdevprem.runtrack.domain.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.domain.tracking.timer.TimeTracker
import com.sdevprem.runtrack.shared.createDataStore
import com.sdevprem.runtrack.shared.data.db.RunTrackDB
import com.sdevprem.runtrack.shared.data.db.dao.RunDao
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import dagger.Binds
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.plus
import javax.inject.Qualifier
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
            listOf(
                object : DataMigration<Preferences> {
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
                }
            )
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

        private fun mapOldToNewPrefs(
            oldData: Map<Preferences.Key<*>, Any>,
            currentMutablePrefs: MutablePreferences
        ) {
            oldData.forEach { (key, value) ->
                when (value) {
                    is Boolean ->
                        currentMutablePrefs[booleanPreferencesKey(key.name)] = value

                    is Float ->
                        currentMutablePrefs[floatPreferencesKey(key.name)] = value
                }
            }
        }

    }

    @Binds
    @Singleton
    abstract fun provideBackgroundTrackingManager(
        trackingServiceManager: DefaultBackgroundTrackingManager
    ): BackgroundTrackingManager

    @Binds
    @Singleton
    abstract fun provideTimeTracker(
        timeTracker: DefaultTimeTracker
    ): TimeTracker


}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OldPrefs

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Prefs