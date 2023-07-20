package com.sdevprem.runtrack.ui.screen.currentrun

import androidx.lifecycle.ViewModel
import com.sdevprem.runtrack.core.tracking.TrackingManager
import com.sdevprem.runtrack.core.tracking.model.PathPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CurrentRunViewModel @Inject constructor(
    private val trackingManager: TrackingManager
) : ViewModel() {
    val pathPoints: StateFlow<List<PathPoint>> = trackingManager.pathPoints
    val isRunning = trackingManager.isTracking

    fun playPauseTracking() {
        if (isRunning.value)
            trackingManager.pauseTracking()
        else trackingManager.startResumeTracking()
    }

    override fun onCleared() {
        super.onCleared()
        trackingManager.stop()
    }
}