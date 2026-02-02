package com.sdevprem.runtrack.shared.data.tracking.location

import com.sdevprem.runtrack.shared.domain.tracking.location.LocationTrackingManager
import com.sdevprem.runtrack.shared.domain.tracking.model.LocationInfo
import com.sdevprem.runtrack.shared.domain.tracking.model.LocationTrackingInfo
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLDistanceFilterNone
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.darwin.NSObject

class DefaultLocationTrackingManager(
    private val locationManager: CLLocationManager
) : LocationTrackingManager {

    private var locationCallback: LocationTrackingManager.LocationCallback? = null
    private var locationDelegate: CLLocationManagerDelegateProtocol? = null

    @OptIn(ExperimentalForeignApi::class)
    override fun setCallback(locationCallback: LocationTrackingManager.LocationCallback) {
        this.locationCallback = locationCallback

        locationDelegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val locations = didUpdateLocations.filterIsInstance<CLLocation>()
                val trackingInfoList = locations.map { location ->
                    val (lat, long) = location.coordinate().useContents {
                        latitude to longitude
                    }
                    LocationTrackingInfo(
                        locationInfo = LocationInfo(
                            latitude = lat,
                            longitude = long
                        ),
                        speedInMS = if (location.speed >= 0) location.speed.toFloat() else 0f
                    )
                }
                if (trackingInfoList.isNotEmpty()) {
                    locationCallback.onLocationUpdate(trackingInfoList)
                }
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: platform.Foundation.NSError) {
                // Handle error - could log or notify callback
            }
        }

        locationManager.delegate = locationDelegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter = kCLDistanceFilterNone
        locationManager.allowsBackgroundLocationUpdates = true
        locationManager.pausesLocationUpdatesAutomatically = false

        // Request permission if needed
        locationManager.requestAlwaysAuthorization()
        locationManager.startUpdatingLocation()
    }

    override fun removeCallback() {
        locationCallback = null
        locationManager.stopUpdatingLocation()
        locationManager.delegate = null
        locationDelegate = null
    }
}