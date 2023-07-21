package com.sdevprem.runtrack.core.tracking.notification

import androidx.core.app.NotificationCompat

interface NotificationHelper {
    val baseNotificationBuilder: NotificationCompat.Builder

    fun createNotificationChannel()
    fun updateTrackingNotification(durationInMillis: Long, isTracking: Boolean)
    fun removeTrackingNotification()

    companion object {
        const val TRACKING_NOTIFICATION_ID = 3
    }
}