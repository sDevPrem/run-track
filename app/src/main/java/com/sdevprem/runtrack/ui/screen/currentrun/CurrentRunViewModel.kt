package com.sdevprem.runtrack.ui.screen.currentrun

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.di.ApplicationScope
import com.sdevprem.runtrack.di.IoDispatcher
import com.sdevprem.runtrack.shared.common.extension.now
import com.sdevprem.runtrack.shared.data.model.Run
import com.sdevprem.runtrack.shared.data.repository.AppRepository
import com.sdevprem.runtrack.shared.domain.model.CurrentRunStateWithCalories
import com.sdevprem.runtrack.shared.domain.tracking.TrackingManager
import com.sdevprem.runtrack.shared.domain.usecase.GetCurrentRunStateWithCaloriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class CurrentRunViewModel @Inject constructor(
    private val trackingManager: TrackingManager,
    private val repository: AppRepository,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    getCurrentRunStateWithCaloriesUseCase: GetCurrentRunStateWithCaloriesUseCase
) : ViewModel() {
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
                    .toBigDecimal()
                    .multiply(3600.toBigDecimal())
                    .divide(runningDurationInMillis.value.toBigDecimal(), 2, RoundingMode.HALF_UP)
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