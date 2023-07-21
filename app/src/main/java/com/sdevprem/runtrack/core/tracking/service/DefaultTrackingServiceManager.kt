package com.sdevprem.runtrack.core.tracking.service

import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultTrackingServiceManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TrackingServiceManager {

    override fun startService() {
        Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_START_SERVICE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(this)
            } else context.startService(this)
        }
    }

    override fun stopService() {
        Intent(context, TrackingService::class.java).apply {
            context.stopService(this)
        }
    }
}