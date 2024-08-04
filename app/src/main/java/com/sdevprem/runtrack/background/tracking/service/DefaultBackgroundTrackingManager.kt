package com.sdevprem.runtrack.background.tracking.service

import android.content.Context
import android.content.Intent
import android.os.Build
import com.sdevprem.runtrack.domain.tracking.background.BackgroundTrackingManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultBackgroundTrackingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : BackgroundTrackingManager {

    override fun startBackgroundTracking() {
        Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_START_SERVICE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(this)
            } else {
                context.startService(this)
            }
        }
    }

    override fun stopBackgroundTracking() {
        Intent(context, TrackingService::class.java).apply(context::stopService)
    }
}