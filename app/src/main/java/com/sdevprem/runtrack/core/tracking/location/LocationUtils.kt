package com.sdevprem.runtrack.core.tracking.location

import android.app.Activity
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient

object LocationUtils {
    private const val LOCATION_UPDATE_INTERVAL = 5000L

    val locationRequestBuilder
        get() = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        )

    const val LOCATION_ENABLE_REQUEST_CODE = 5234

    fun checkAndRequestLocationSetting(activity: Activity) {
        val locationRequest = locationRequestBuilder.build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(activity)

        client.checkLocationSettings(builder.build())
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(
                            activity,
                            LOCATION_ENABLE_REQUEST_CODE
                        )
                    } catch (_: IntentSender.SendIntentException) {
                    }
                }
            }
    }
}