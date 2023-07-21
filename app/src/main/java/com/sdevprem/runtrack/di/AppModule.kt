package com.sdevprem.runtrack.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.sdevprem.runtrack.core.tracking.location.DefaultLocationTrackingManager
import com.sdevprem.runtrack.core.tracking.location.LocationTrackingManager
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