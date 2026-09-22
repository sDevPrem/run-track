package com.sdevprem.runtrack.shared.common.utils

import com.sdevprem.runtrack.shared.domain.tracking.model.PathPoint

expect object LocationUtils {

    fun getDistanceBetweenPathPoints(
        pathPoint1: PathPoint,
        pathPoint2: PathPoint
    ): Int
}