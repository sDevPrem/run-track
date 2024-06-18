package com.sdevprem.runtrack.common.utils

import android.location.Location
import com.sdevprem.runtrack.core.tracking.model.PathPoint
import kotlin.math.roundToInt

object LocationUtils {

    fun getDistanceBetweenPathPoints(
        pathPoint1: PathPoint,
        pathPoint2: PathPoint
    ): Int {
        return if (pathPoint1 is PathPoint.LocationPoint && pathPoint2 is PathPoint.LocationPoint) {
            val result = FloatArray(1)
            Location.distanceBetween(
                pathPoint1.latLng.latitude,
                pathPoint1.latLng.longitude,
                pathPoint2.latLng.latitude,
                pathPoint2.latLng.longitude,
                result
            )
            result[0].roundToInt()
        } else 0
    }

    fun calculateDistanceCovered(pathPoints: List<PathPoint>): Int {
        var distance = 0
        pathPoints.forEachIndexed { i, pathPoint ->
            if (i == pathPoints.size - 1)
                return@forEachIndexed
            distance += getDistanceBetweenPathPoints(pathPoint, pathPoints[i + 1])
        }
        return distance
    }
}