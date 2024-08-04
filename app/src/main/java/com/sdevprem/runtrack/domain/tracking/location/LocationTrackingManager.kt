package com.sdevprem.runtrack.domain.tracking.location

import com.sdevprem.runtrack.domain.tracking.model.LocationTrackingInfo

interface LocationTrackingManager {
    fun setCallback(locationCallback: LocationCallback)

    fun removeCallback()

    interface LocationCallback {
        fun onLocationUpdate(results: List<LocationTrackingInfo>)
    }
}