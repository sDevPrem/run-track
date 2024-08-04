package com.sdevprem.runtrack

import android.app.Application
import com.sdevprem.runtrack.background.tracking.service.notification.TrackingNotificationHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class RunTrackApp : Application() {
    @Inject
    lateinit var notificationHelper: TrackingNotificationHelper
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        notificationHelper.createNotificationChannel()
    }
}