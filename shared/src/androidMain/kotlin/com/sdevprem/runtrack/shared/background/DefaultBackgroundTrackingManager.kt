package com.sdevprem.runtrack.shared.background

import android.content.Context
import android.content.Intent
import android.os.Build
import com.sdevprem.runtrack.shared.background.tracking.service.TrackingService
import com.sdevprem.runtrack.shared.domain.tracking.background.BackgroundTrackingManager

class DefaultBackgroundTrackingManager(
    private val context: Context
) : BackgroundTrackingManager {

    override fun startBackgroundTracking() {
        Intent(context, TrackingService::class.java).apply {
            action = TrackingService.Companion.ACTION_START_SERVICE
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