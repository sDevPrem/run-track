package com.sdevprem.runtrack.core.tracking

import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.sdevprem.runtrack.common.utils.LocationUtils
import com.sdevprem.runtrack.core.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.core.tracking.model.CurrentRunState
import com.sdevprem.runtrack.core.tracking.model.PathPoint
import com.sdevprem.runtrack.core.tracking.service.TrackingServiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingManager @Inject constructor(
    private val locationTrackingManager: LocationTrackingManager,
    private val timeTracker: TimeTracker,
    private val trackingServiceManager: TrackingServiceManager
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

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (isTracking) {
                result.locations.forEach { location ->
                    addPathPoints(location)
                    Timber.d("New LocationPoint : ${location.latitude}, ${location.longitude}")
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

    private fun addPathPoints(location: Location?) = location?.let {
        val pos = LatLng(it.latitude, it.longitude)
        _currentRunState.update { state ->
            val pathPoints = state.pathPoints + PathPoint.LocationPoint(pos)
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
                speedInKMH = (it.speed * 3.6f).toBigDecimal()
                    .setScale(2, RoundingMode.HALF_UP).toFloat()
            )
        }
    }

    fun startResumeTracking() {
        if (isTracking)
            return
        if (isFirst) {
            postInitialValue()
            trackingServiceManager.startService()
            isFirst = false
        }
        isTracking = true
        timeTracker.startResumeTimer(timeTrackerCallback)
        locationTrackingManager.registerCallback(locationCallback)
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
        locationTrackingManager.unRegisterCallback(locationCallback)
        timeTracker.pauseTimer()
        addEmptyPolyLine()
    }

    fun stop() {
        pauseTracking()
        trackingServiceManager.stopService()
        timeTracker.stopTimer()
        postInitialValue()
        isFirst = true
    }

}