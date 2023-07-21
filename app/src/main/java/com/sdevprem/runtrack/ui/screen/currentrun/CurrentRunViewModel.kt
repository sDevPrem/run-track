package com.sdevprem.runtrack.ui.screen.currentrun

import androidx.lifecycle.ViewModel
import com.sdevprem.runtrack.core.tracking.TrackingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CurrentRunViewModel @Inject constructor(
    private val trackingManager: TrackingManager
) : ViewModel() {
    val currentRunState = trackingManager.currentRunState
    val runningDurationInMillis = trackingManager.trackingDurationInMs

    fun playPauseTracking() {
        if (currentRunState.value.isTracking)
            trackingManager.pauseTracking()
        else trackingManager.startResumeTracking()
    }

    fun finishRun() {
        trackingManager.stop()
    }
}