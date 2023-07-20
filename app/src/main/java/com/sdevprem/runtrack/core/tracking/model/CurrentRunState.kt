package com.sdevprem.runtrack.core.tracking.model

data class CurrentRunState(
    val distanceInMeters: Int = 0,
    val caloriesBurnt: Int = 0,
    val speedInKMH: Int = 0,
    val isTracking: Boolean = false,
    val pathPoints: List<PathPoint> = emptyList()
)
