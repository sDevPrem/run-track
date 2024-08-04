package com.sdevprem.runtrack.data.tracking.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.sdevprem.runtrack.common.extension.hasLocationPermission
import com.sdevprem.runtrack.domain.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.domain.tracking.model.LocationInfo
import com.sdevprem.runtrack.domain.tracking.model.LocationTrackingInfo
import dagger.hilt.android.qualifiers.ApplicationContext


@SuppressLint("MissingPermission")
class DefaultLocationTrackingManager constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context,
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
        if (context.hasLocationPermission()) {
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