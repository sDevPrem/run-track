package com.sdevprem.runtrack.core.tracking.model

sealed interface PathPoint {
    data class LocationPoint(val locationInfo: LocationInfo) : PathPoint
    data object EmptyLocationPoint : PathPoint
}

fun List<PathPoint>.lasLocationPoint(): PathPoint.LocationPoint? {
    for (i in lastIndex downTo 0)
        if (get(i) is PathPoint.LocationPoint)
            return get(i) as PathPoint.LocationPoint
    return null
}

fun List<PathPoint>.firstLocationPoint() =
    find { it is PathPoint.LocationPoint } as? PathPoint.LocationPoint

