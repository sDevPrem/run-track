package com.sdevprem.runtrack.shared.ui.screen.currentrun

import com.sdevprem.runtrack.shared.common.extension.now
import com.sdevprem.runtrack.shared.common.extension.roundTo
import com.sdevprem.runtrack.shared.data.model.Run
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.domain.model.CurrentRunStateWithCalories
import com.sdevprem.runtrack.shared.domain.tracking.TrackingManager
import com.sdevprem.runtrack.shared.domain.usecase.GetCurrentRunStateWithCaloriesUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class CurrentRunViewModel(
    private val trackingManager: TrackingManager,
    private val repository: AppRepository,
    private val appCoroutineScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    viewModelScope: CoroutineScope,
    getCurrentRunStateWithCaloriesUseCase: GetCurrentRunStateWithCaloriesUseCase
) {
    val currentRunStateWithCalories = getCurrentRunStateWithCaloriesUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            CurrentRunStateWithCalories()
        )
    val runningDurationInMillis = trackingManager.trackingDurationInMs

    fun playPauseTracking() {
        if (currentRunStateWithCalories.value.currentRunState.isTracking)
            trackingManager.pauseTracking()
        else trackingManager.startResumeTracking()
    }

    fun finishRun(trackImg: ByteArray) {
        trackingManager.pauseTracking()
        saveRun(
            Run(
                img = trackImg,
                avgSpeedInKMH = currentRunStateWithCalories.value.currentRunState.distanceInMeters
                    .times(3600f)
                    .div(runningDurationInMillis.value)
                    .roundTo(2)
                    .toFloat(),
                distanceInMeters = currentRunStateWithCalories.value.currentRunState.distanceInMeters,
                durationInMillis = runningDurationInMillis.value,
                timestamp = LocalDateTime.now(),
                caloriesBurned = currentRunStateWithCalories.value.caloriesBurnt
            )
        )
        trackingManager.stop()
    }

    private fun saveRun(run: Run) = appCoroutineScope.launch(ioDispatcher) {
        repository.insertRun(run)
    }

}