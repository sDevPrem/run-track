package com.sdevprem.runtrack.core.tracking.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.sdevprem.runtrack.utils.RunUtils.hasLocationPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val LOCATION_UPDATE_INTERVAL = 5000L

@SuppressLint("MissingPermission")
class DefaultLocationTrackingManager @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) : LocationTrackingManager {

    override fun registerCallback(locationCallback: LocationCallback) {
        if (context.hasLocationPermission()) {
            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                LOCATION_UPDATE_INTERVAL
            ).build()

            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun unRegisterCallback(locationCallback: LocationCallback) {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}