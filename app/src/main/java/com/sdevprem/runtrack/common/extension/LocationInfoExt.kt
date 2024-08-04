package com.sdevprem.runtrack.common.extension

import com.google.android.gms.maps.model.LatLng
import com.sdevprem.runtrack.domain.tracking.model.LocationInfo

fun LocationInfo.toLatLng() = LatLng(
    latitude,
    longitude
)