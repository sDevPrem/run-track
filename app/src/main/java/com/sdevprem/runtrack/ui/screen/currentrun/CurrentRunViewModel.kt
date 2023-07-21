package com.sdevprem.runtrack.ui.screen.currentrun

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.core.data.repository.AppRepository
import com.sdevprem.runtrack.core.tracking.TrackingManager
import com.sdevprem.runtrack.di.ApplicationScope
import com.sdevprem.runtrack.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CurrentRunViewModel @Inject constructor(
    private val trackingManager: TrackingManager,
    private val repository: AppRepository,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val currentRunState = trackingManager.currentRunState
    val runningDurationInMillis = trackingManager.trackingDurationInMs

    fun playPauseTracking() {
        if (currentRunState.value.isTracking)
            trackingManager.pauseTracking()
        else trackingManager.startResumeTracking()
    }

    fun finishRun(bitmap: Bitmap) {
        trackingManager.pauseTracking()
        saveRun(
            Run(
                img = bitmap,
                avgSpeedInKMH = currentRunState.value.distanceInMeters.toBigDecimal()
                    .multiply(3600.toBigDecimal())
                    .divide(runningDurationInMillis.value.toBigDecimal(), 2, RoundingMode.HALF_UP)
                    .toFloat(),
                distanceInMeters = currentRunState.value.distanceInMeters,
                durationInMillis = runningDurationInMillis.value,
                timestamp = Date(),
            )
        )
        trackingManager.stop()
    }

    private fun saveRun(run: Run) = appCoroutineScope.launch(ioDispatcher) {
        repository.insertRun(run)
    }

}