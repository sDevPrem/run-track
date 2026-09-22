package com.sdevprem.runtrack.shared.common.utils

import com.sdevprem.runtrack.shared.domain.tracking.model.PathPoint
import platform.CoreLocation.CLLocation
import kotlin.math.roundToInt

actual object LocationUtils {

    actual fun getDistanceBetweenPathPoints(
        pathPoint1: PathPoint,
        pathPoint2: PathPoint
    ): Int {
        return if (pathPoint1 is PathPoint.LocationPoint && pathPoint2 is PathPoint.LocationPoint) {
            val location1 = CLLocation(
                latitude = pathPoint1.locationInfo.latitude,
                longitude = pathPoint1.locationInfo.longitude
            )
            val location2 = CLLocation(
                latitude = pathPoint2.locationInfo.latitude,
                longitude = pathPoint2.locationInfo.longitude
            )
            location1.distanceFromLocation(location2).roundToInt()
        } else 0
    }
}