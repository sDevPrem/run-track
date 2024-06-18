package com.sdevprem.runtrack.core.tracking.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.sdevprem.runtrack.common.extension.hasLocationPermission
import dagger.hilt.android.qualifiers.ApplicationContext


@SuppressLint("MissingPermission")
class DefaultLocationTrackingManager constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context,
    private val locationRequest: LocationRequest
) : LocationTrackingManager {

    override fun registerCallback(locationCallback: LocationCallback) {
        if (context.hasLocationPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun unRegisterCallback(locationCallback: LocationCallback) {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}