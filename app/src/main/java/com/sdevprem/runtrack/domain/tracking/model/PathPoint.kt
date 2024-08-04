package com.sdevprem.runtrack.domain.tracking.model

sealed interface PathPoint {
    data class LocationPoint(val locationInfo: LocationInfo) : PathPoint
    data object EmptyLocationPoint : PathPoint
}

fun List<PathPoint>.lasLocationPoint() =
    findLast { it is PathPoint.LocationPoint } as? PathPoint.LocationPoint

fun List<PathPoint>.firstLocationPoint() =
    find { it is PathPoint.LocationPoint } as? PathPoint.LocationPoint

