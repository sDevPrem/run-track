package com.sdevprem.runtrack.shared.data.tracking.location

import com.sdevprem.runtrack.shared.domain.location.LocationTrackingManager
import com.sdevprem.runtrack.shared.domain.model.LocationInfo
import com.sdevprem.runtrack.shared.domain.model.LocationTrackingInfo
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLActivityTypeFitness
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLDistanceFilterNone
import platform.CoreLocation.kCLLocationAccuracyBestForNavigation
import platform.darwin.NSObject

class DefaultLocationTrackingManager : LocationTrackingManager {

    private var locationCallback: LocationTrackingManager.LocationCallback? = null
    private val locationManager = CLLocationManager()
    private val iosLocationCallback = object : NSObject(), CLLocationManagerDelegateProtocol {

        @OptIn(ExperimentalForeignApi::class)
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            print("locationManagerInvoked")
            didUpdateLocations
                .mapNotNull {
                    if (it !is CLLocation) return@mapNotNull null
                    it.coordinate.useContents {
                        val locationInfo = LocationInfo(latitude, longitude)
                        return@mapNotNull LocationTrackingInfo(locationInfo, it.speed.toFloat())
                    }
                }.also { locationCallback?.onLocationUpdate(it) }
        }

    }

    override fun setCallback(locationCallback: LocationTrackingManager.LocationCallback) {
        this.locationCallback = locationCallback
        locationManager.requestWhenInUseAuthorization()
        locationManager.delegate = iosLocationCallback

        locationManager.pausesLocationUpdatesAutomatically = true
        locationManager.showsBackgroundLocationIndicator = true
        locationManager.allowsBackgroundLocationUpdates = true
        locationManager.distanceFilter = kCLDistanceFilterNone
        locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        locationManager.activityType = CLActivityTypeFitness
        locationManager.startUpdatingLocation()
    }

    override fun removeCallback() {
        locationCallback = null
        locationManager.stopUpdatingLocation()
    }
}