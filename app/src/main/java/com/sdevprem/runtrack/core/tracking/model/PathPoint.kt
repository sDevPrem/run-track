package com.sdevprem.runtrack.core.tracking.model

import com.google.android.gms.maps.model.LatLng

sealed interface PathPoint {
    class LocationPoint(val latLng: LatLng) : PathPoint
    object EmptyLocationPoint : PathPoint
}

fun List<PathPoint>.lasLocationPoint(): PathPoint.LocationPoint? {
    for (i in lastIndex downTo 0)
        if (get(i) is PathPoint.LocationPoint)
            return get(i) as PathPoint.LocationPoint
    return null
}

fun List<PathPoint>.firstLocationPoint() =
    find { it is PathPoint.LocationPoint } as? PathPoint.LocationPoint

