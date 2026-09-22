package com.sdevprem.runtrack.shared.data.tracking.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.sdevprem.runtrack.shared.domain.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.model.LocationInfo
import com.sdevprem.runtrack.shared.domain.tracking.model.LocationTrackingInfo

@SuppressLint("MissingPermission")
class DefaultLocationTrackingManager(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context,
    private val locationRequest: LocationRequest
) : LocationTrackingManager {

    private var locationCallback: LocationTrackingManager.LocationCallback? = null
    private val gLocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            locationCallback?.onLocationUpdate(
                p0.locations.mapNotNull {
                    it?.let {
                        LocationTrackingInfo(
                            locationInfo = LocationInfo(it.latitude, it.longitude),
                            speedInMS = it.speed
                        )
                    }
                }
            )
        }
    }

    override fun setCallback(locationCallback: LocationTrackingManager.LocationCallback) {
        if (/*context.hasLocationPermission()*/ true) { //todo: add permission check
            this.locationCallback = locationCallback
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                gLocationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun removeCallback() {
        this.locationCallback = null
        fusedLocationProviderClient.removeLocationUpdates(gLocationCallback)
    }

}