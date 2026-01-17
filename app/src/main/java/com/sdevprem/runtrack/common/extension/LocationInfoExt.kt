package com.sdevprem.runtrack.common.extension

import com.google.android.gms.maps.model.LatLng
import com.sdevprem.runtrack.shared.domain.tracking.model.LocationInfo

fun LocationInfo.toLatLng() = LatLng(
    latitude,
    longitude
)