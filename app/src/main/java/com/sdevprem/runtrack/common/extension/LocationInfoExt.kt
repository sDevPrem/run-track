package com.sdevprem.runtrack.common.extension

import com.google.android.gms.maps.model.LatLng
import com.sdevprem.runtrack.core.tracking.model.LocationInfo

fun LocationInfo.toLatLng() = LatLng(
    latitude,
    longitude
)