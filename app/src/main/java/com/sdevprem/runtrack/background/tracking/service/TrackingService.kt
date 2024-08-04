package com.sdevprem.runtrack.background.tracking.service

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.sdevprem.runtrack.background.tracking.service.notification.TrackingNotificationHelper
import com.sdevprem.runtrack.domain.tracking.TrackingManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    companion object {
        const val ACTION_PAUSE_TRACKING = "action_pause_tracking"
        const val ACTION_RESUME_TRACKING = "action_resume_tracking"
        const val ACTION_START_SERVICE = "action_start_service"
    }

    @Inject
    lateinit var trackingManager: TrackingManager

    @Inject
    lateinit var notificationHelper: TrackingNotificationHelper
    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_PAUSE_TRACKING -> trackingManager.pauseTracking()
            ACTION_RESUME_TRACKING -> trackingManager.startResumeTracking()
            ACTION_START_SERVICE -> {
                startForeground(
                    TrackingNotificationHelper.TRACKING_NOTIFICATION_ID,
                    notificationHelper.getDefaultNotification()
                )

                if (job == null)
                    job = combine(
                        trackingManager.trackingDurationInMs,
                        trackingManager.currentRunState
                    ) { duration, currentRunState ->
                        notificationHelper.updateTrackingNotification(
                            durationInMillis = duration,
                            isTracking = currentRunState.isTracking
                        )
                    }.launchIn(lifecycleScope)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationHelper.removeTrackingNotification()

        job?.cancel()
        job = null
    }
}