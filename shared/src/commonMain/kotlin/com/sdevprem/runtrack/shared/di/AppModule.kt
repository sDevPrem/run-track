package com.sdevprem.runtrack.shared.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sdevprem.runtrack.shared.data.db.RunTrackDB
import com.sdevprem.runtrack.shared.data.db.dao.RunDao
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.data.repository.UserRepository
import com.sdevprem.runtrack.shared.data.tracking.timer.DefaultTimeTracker
import com.sdevprem.runtrack.shared.data.utils.LocalFileProcessor
import com.sdevprem.runtrack.shared.domain.tracking.TrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.background.BackgroundTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.timer.TimeTracker
import com.sdevprem.runtrack.shared.domain.usecase.GetCurrentRunStateWithCaloriesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.sdevprem.runtrack.shared")
class AppModule {

    companion object {
        const val USER_PREFERENCES_FILE_NAME = "user_preferences"
    }

    @Single
    fun provideCoroutineDispatchers(): CoroutineDispatchers {
        return CoroutineDispatchers(
            io = Dispatchers.IO,
            main = Dispatchers.Main,
            default = Dispatchers.Default,
            mainImmediate = Dispatchers.Main.immediate
        )
    }

    @Single
    @Named("ApplicationScope")
    fun providesCoroutineScope(
        dispatchers: CoroutineDispatchers
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatchers.default)

    @Single
    fun provideUserRepository(
        datastore: DataStore<Preferences>,
        localFileProcessor: LocalFileProcessor
    ) = UserRepository(datastore, localFileProcessor)

    @Single
    fun provideSharedRunDao(db: RunTrackDB) =
        db.getRunDao()


    @Single
    fun provideAppRepository(
        runDao: RunDao
    ): AppRepository = AppRepository(runDao = runDao)

    @Single
    fun providesTimeTracker(
        @Named("ApplicationScope") scope: CoroutineScope,
        dispatcher: CoroutineDispatchers
    ): TimeTracker = DefaultTimeTracker(scope, dispatcher.io)

    @Single
    fun providesTrackingManager(
        locationTrackingManager: LocationTrackingManager,
        timeTracker: TimeTracker,
        backgroundTrackingManager: BackgroundTrackingManager
    ) = TrackingManager(
        locationTrackingManager = locationTrackingManager,
        timeTracker = timeTracker,
        backgroundTrackingManager = backgroundTrackingManager
    )

    @Single
    fun providesGetCurrentRunStateWithCaloriesUseCase(
        trackingManager: TrackingManager,
        userRepository: UserRepository
    ) = GetCurrentRunStateWithCaloriesUseCase(userRepository, trackingManager)
}