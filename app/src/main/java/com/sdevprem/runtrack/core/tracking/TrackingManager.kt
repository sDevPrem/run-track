package com.sdevprem.runtrack.core.tracking

import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.sdevprem.runtrack.core.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.core.tracking.model.CurrentRunState
import com.sdevprem.runtrack.core.tracking.model.PathPoint
import com.sdevprem.runtrack.utils.RunUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.math.RoundingMode
import javax.inject.Inject

class TrackingManager @Inject constructor(
    private val locationTrackingManager: LocationTrackingManager
) {
    private var isTracking = false
        set(value) {
            _currentRunState.update { it.copy(isTracking = value) }
            field = value
        }
    private val _currentRunState = MutableStateFlow(CurrentRunState())
    val currentRunState = _currentRunState

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
                        distance += RunUtils.getDistanceBetweenPathPoints(
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
        if (isFirst) {
            postInitialValue()
            isFirst = false
        }
        isTracking = true
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
        addEmptyPolyLine()
    }

    fun stop() {
        postInitialValue()
        locationTrackingManager.unRegisterCallback(locationCallback)
    }

}