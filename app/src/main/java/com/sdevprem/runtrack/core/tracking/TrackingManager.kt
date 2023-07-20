package com.sdevprem.runtrack.core.tracking

import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.sdevprem.runtrack.core.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.core.tracking.model.PathPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

class TrackingManager @Inject constructor(
    private val locationTrackingManager: LocationTrackingManager
) {
    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _pathPoints = MutableStateFlow(listOf<PathPoint>())
    val pathPoints = _pathPoints.asStateFlow()

    private var isFirst = true

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (isTracking.value) {
                result.locations.forEach { location ->
                    addPathPoints(location)
                    Timber.d("New LocationPoint : ${location.latitude}, ${location.longitude}")
                }
            }
        }
    }

    private fun postInitialValue() {
        _isTracking.update { false }
        _pathPoints.update { emptyList() }
    }

    private fun addPathPoints(location: Location?) = location?.let {
        val pos = LatLng(it.latitude, it.longitude)
        _pathPoints.update { pathPoints ->
            pathPoints + PathPoint.LocationPoint(pos)
        }
    }

    fun startResumeTracking() {
        if (isFirst) {
            postInitialValue()
            isFirst = false
        }
        _isTracking.update { true }
        locationTrackingManager.registerCallback(locationCallback)
    }

    private fun addEmptyPolyLine() {
        _pathPoints.update {
            it + PathPoint.EmptyLocationPoint
        }
    }

    fun pauseTracking() {
        _isTracking.update { false }
        locationTrackingManager.unRegisterCallback(locationCallback)
        addEmptyPolyLine()
    }

    fun stop() {
        postInitialValue()
        locationTrackingManager.unRegisterCallback(locationCallback)
    }

}