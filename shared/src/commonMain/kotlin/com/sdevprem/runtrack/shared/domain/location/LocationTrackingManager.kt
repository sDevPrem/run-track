package com.sdevprem.runtrack.shared.domain.location

import com.sdevprem.runtrack.shared.domain.model.LocationTrackingInfo

interface LocationTrackingManager {
    fun setCallback(locationCallback: LocationCallback)

    fun removeCallback()

    interface LocationCallback {
        fun onLocationUpdate(results: List<LocationTrackingInfo>)
    }
}