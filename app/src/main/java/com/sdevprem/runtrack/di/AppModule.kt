package com.sdevprem.runtrack.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.sdevprem.runtrack.core.tracking.location.DefaultLocationTrackingManager
import com.sdevprem.runtrack.core.tracking.location.LocationTrackingManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object {
        @Singleton
        @Provides
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ) = LocationServices
            .getFusedLocationProviderClient(context)
    }

    @Binds
    @Singleton
    abstract fun provideLocationTrackingManager(
        defaultLocationTrackingManager: DefaultLocationTrackingManager
    ): LocationTrackingManager

}