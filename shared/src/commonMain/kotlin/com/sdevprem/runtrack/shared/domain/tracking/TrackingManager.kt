package com.sdevprem.runtrack.shared.domain.tracking

import com.sdevprem.runtrack.shared.common.utils.LocationUtils
import com.sdevprem.runtrack.shared.domain.tracking.background.BackgroundTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.model.CurrentRunState
import com.sdevprem.runtrack.shared.domain.tracking.model.LocationTrackingInfo
import com.sdevprem.runtrack.shared.domain.tracking.model.PathPoint
import com.sdevprem.runtrack.shared.domain.tracking.timer.TimeTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.round

class TrackingManager(
    private val locationTrackingManager: LocationTrackingManager,
    private val timeTracker: TimeTracker,
    private val backgroundTrackingManager: BackgroundTrackingManager
) {
    private var isTracking = false
        set(value) {
            _currentRunState.update { it.copy(isTracking = value) }
            field = value
        }

    private val _currentRunState = MutableStateFlow(CurrentRunState())
    val currentRunState = _currentRunState

    private val _trackingDurationInMs = MutableStateFlow(0L)
    val trackingDurationInMs = _trackingDurationInMs.asStateFlow()

    private val timeTrackerCallback = { timeElapsed: Long ->
        _trackingDurationInMs.update { timeElapsed }
    }

    private var isFirst = true

    private val locationCallback = object : LocationTrackingManager.LocationCallback {

        override fun onLocationUpdate(results: List<LocationTrackingInfo>) {
            if (isTracking) {
                results.forEach { info ->
                    addPathPoints(info)
                }
            }
        }
    }

    private fun postInitialValue() {
        _currentRunState.update {
            CurrentRunState()
        }
        _trackingDurationInMs.update { 0 }
    }

    private fun addPathPoints(info: LocationTrackingInfo) {
        _currentRunState.update { state ->
            val pathPoints = state.pathPoints + PathPoint.LocationPoint(info.locationInfo)
            state.copy(
                pathPoints = pathPoints,
                distanceInMeters = state.distanceInMeters.run {
                    var distance = this
                    if (pathPoints.size > 1)
                        distance += LocationUtils.getDistanceBetweenPathPoints(
                            pathPoint1 = pathPoints[pathPoints.size - 1],
                            pathPoint2 = pathPoints[pathPoints.size - 2]
                        )
                    distance
                },
                speedInKMH = round(info.speedInMS * 3.6f * 100f) / 100f
            )
        }
    }

    fun startResumeTracking() {
        if (isTracking)
            return
        if (isFirst) {
            postInitialValue()
            backgroundTrackingManager.startBackgroundTracking()
            isFirst = false
        }
        isTracking = true
        timeTracker.startResumeTimer(timeTrackerCallback)
        locationTrackingManager.setCallback(locationCallback)
    }

    private fun addEmptyPolyLine() {
        _currentRunState.update {
            it.copy(
                pathPoints = it.pathPoints + PathPoint.EmptyLocationPoint
            )
        }
    }

    fun pauseTracking() {
        isTracking = false
        locationTrackingManager.removeCallback()
        timeTracker.pauseTimer()
        addEmptyPolyLine()
    }

    fun stop() {
        pauseTracking()
        backgroundTrackingManager.stopBackgroundTracking()
        timeTracker.stopTimer()
        postInitialValue()
        isFirst = true
    }

}