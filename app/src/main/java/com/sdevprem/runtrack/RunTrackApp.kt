package com.sdevprem.runtrack

import android.app.Application
import com.sdevprem.runtrack.shared.background.notification.TrackingNotificationHelper
import com.sdevprem.runtrack.shared.di.AppModule
import com.sdevprem.runtrack.shared.di.PlatformModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.module
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
        startKoin {
            androidContext(this@RunTrackApp)
            androidLogger()
            modules(PlatformModule().module)
            modules(AppModule().module)
        }
    }
}