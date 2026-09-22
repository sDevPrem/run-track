package com.sdevprem.runtrack.shared.background

import com.sdevprem.runtrack.shared.domain.tracking.background.BackgroundTrackingManager
import platform.UIKit.UIApplication
import platform.UIKit.UIBackgroundTaskIdentifier
import platform.UIKit.UIBackgroundTaskInvalid

/**
 * iOS implementation of [BackgroundTrackingManager].
 *
 * On iOS, background location tracking is enabled through:
 * 1. Configuring Info.plist with "location" background mode (UIBackgroundModes)
 * 2. Setting CLLocationManager.allowsBackgroundLocationUpdates = true (done in DefaultLocationTrackingManager)
 * 3. Requesting "Always" location authorization
 *
 * This class uses UIApplication's background task API to request additional
 * background execution time when needed, though location updates will continue
 * as long as the background mode is properly configured.
 */
class DefaultBackgroundTrackingManager : BackgroundTrackingManager {

    private var backgroundTaskId: UIBackgroundTaskIdentifier = UIBackgroundTaskInvalid

    override fun startBackgroundTracking() {
        // Request extended background execution time
        // This helps ensure the app stays alive during tracking
        // Note: iOS may still terminate the app, but location updates will wake it
        backgroundTaskId = UIApplication.sharedApplication.beginBackgroundTaskWithName(
            taskName = "RunTracking"
        ) {
            // Expiration handler - called when background time is about to expire
            endBackgroundTask()
        }
    }

    override fun stopBackgroundTracking() {
        endBackgroundTask()
    }

    private fun endBackgroundTask() {
        if (backgroundTaskId != UIBackgroundTaskInvalid) {
            UIApplication.sharedApplication.endBackgroundTask(backgroundTaskId)
            backgroundTaskId = UIBackgroundTaskInvalid
        }
    }
}