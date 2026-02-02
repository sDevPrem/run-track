package com.sdevprem.runtrack.shared.ui.screen.currentrun.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSMutablePath
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.GoogleMaps.kGMSTypeNormal
import com.sdevprem.runtrack.shared.domain.tracking.model.PathPoint
import com.sdevprem.runtrack.shared.domain.tracking.model.firstLocationPoint
import com.sdevprem.runtrack.shared.domain.tracking.model.lasLocationPoint
import com.sdevprem.runtrack.shared.ui.theme.RTColor
import com.sdevprem.runtrack.shared.ui.theme.md_theme_light_primary
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.readValue
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectZero
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSData
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Map(
    modifier: Modifier,
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    onSnapshot: (ByteArray) -> Unit
) {
    var isMapLoaded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        ShowMapLoadingProgressBar(!isMapLoaded)
        GoogleMapView(
            pathPoints = pathPoints,
            isRunningFinished = isRunningFinished,
            onMapLoaded = { isMapLoaded = true },
            onSnapshot = onSnapshot,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
private fun GoogleMapView(
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    onMapLoaded: () -> Unit,
    onSnapshot: (ByteArray) -> Unit,
    modifier: Modifier = Modifier
) {
    val lastLocationPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.lasLocationPoint() }
    }
    val firstLocationPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.firstLocationPoint() }
    }

    // Default camera position
    val defaultLatitude = 28.4595
    val defaultLongitude = 77.0266
    val defaultZoom = 15f

    var mapViewRef by remember { mutableStateOf<GMSMapView?>(null) }
    var polylines by remember { mutableStateOf<List<GMSPolyline>>(emptyList()) }
    var currentMarker by remember { mutableStateOf<GMSMarker?>(null) }
    var startMarker by remember { mutableStateOf<GMSMarker?>(null) }

    // Update camera when last location changes
    LaunchedEffect(lastLocationPoint) {
        lastLocationPoint?.let { point ->
            mapViewRef?.let { mapView ->
                val newCamera = GMSCameraPosition.cameraWithLatitude(
                    latitude = point.locationInfo.latitude,
                    longitude = point.locationInfo.longitude,
                    zoom = defaultZoom
                )
                mapView.setCamera(newCamera)
            }
        }
    }

    // Update polylines when path points change
    LaunchedEffect(pathPoints) {
        mapViewRef?.let { mapView ->
            // Clear existing polylines
            polylines.forEach { polyline ->
                polyline.map = null
            }

            val newPolylines = mutableListOf<GMSPolyline>()
            var currentPath = GMSMutablePath()

            pathPoints.forEach { pathPoint ->
                when (pathPoint) {
                    is PathPoint.EmptyLocationPoint -> {
                        // Create polyline for current path segment and start new one
                        if (currentPath.count().toInt() > 0) {
                            val polyline = GMSPolyline.polylineWithPath(currentPath)
                            polyline.strokeColor = md_theme_light_primary.toUIColor()
                            polyline.strokeWidth = 5.0
                            polyline.map = mapView
                            newPolylines.add(polyline)
                        }
                        currentPath = GMSMutablePath()
                    }
                    is PathPoint.LocationPoint -> {
                        currentPath.addLatitude(
                            pathPoint.locationInfo.latitude,
                            longitude = pathPoint.locationInfo.longitude
                        )
                    }
                }
            }

            // Add the last path segment
            if (currentPath.count().toInt() > 0) {
                val polyline = GMSPolyline.polylineWithPath(currentPath)
                polyline.strokeColor = md_theme_light_primary.toUIColor()
                polyline.strokeWidth = 5.0
                polyline.map = mapView
                newPolylines.add(polyline)
            }

            polylines = newPolylines
        }
    }

    // Update markers when location points change
    LaunchedEffect(lastLocationPoint, firstLocationPoint, isRunningFinished) {
        mapViewRef?.let { mapView ->
            // Update current position marker
            currentMarker?.map = null
            lastLocationPoint?.let { point ->
                val marker = GMSMarker()
                marker.position = CLLocationCoordinate2DMake(
                    point.locationInfo.latitude,
                    point.locationInfo.longitude
                )

                if (isRunningFinished) {
                    // Use red marker for end position
                    marker.icon = GMSMarker.markerImageWithColor(UIColor.redColor)
                } else {
                    // Use primary color marker for current position
                    marker.icon = GMSMarker.markerImageWithColor(md_theme_light_primary.toUIColor())
                }
                marker.map = mapView
                currentMarker = marker
            }

            // Update start marker
            startMarker?.map = null
            firstLocationPoint?.let { point ->
                val marker = GMSMarker()
                marker.position = CLLocationCoordinate2DMake(
                    point.locationInfo.latitude,
                    point.locationInfo.longitude
                )
                marker.icon = GMSMarker.markerImageWithColor(RTColor.CHATEAU_GREEN.toUIColor())
                marker.map = mapView
                startMarker = marker
            }
        }
    }

    // Take snapshot when running is finished
    LaunchedEffect(isRunningFinished) {
        if (isRunningFinished) {
            mapViewRef?.let { mapView ->
                // Delay to ensure map is rendered
                kotlinx.coroutines.delay(500)

                // Take snapshot using UIGraphicsImageRenderer
                val renderer = UIGraphicsImageRenderer(bounds = mapView.bounds)
                val image = renderer.imageWithActions { context ->
                    mapView.layer.renderInContext(context!!.CGContext)
                }

                // Convert UIImage to ByteArray
                val pngData = UIImagePNGRepresentation(image)
                pngData?.let { data ->
                    val bytes = data.toByteArray()
                    onSnapshot(bytes)
                }
            }
        }
    }

    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            polylines.forEach { polyline -> polyline.map = null }
            currentMarker?.map = null
            startMarker?.map = null
        }
    }

    UIKitView(
        factory = {
            val initialLatitude = lastLocationPoint?.locationInfo?.latitude ?: defaultLatitude
            val initialLongitude = lastLocationPoint?.locationInfo?.longitude ?: defaultLongitude

            val camera = GMSCameraPosition.cameraWithLatitude(
                latitude = initialLatitude,
                longitude = initialLongitude,
                zoom = defaultZoom,
            )

            val mapView = GMSMapView.mapWithFrame(
                frame = CGRectZero.readValue(),
                camera = camera
            )

            mapView.settings().setCompassButton(true)
            mapView.settings().setZoomGestures(true)
            mapView.settings().setScrollGestures(true)
            mapView.setMapType(kGMSTypeNormal)

            mapViewRef = mapView
            onMapLoaded()

            mapView
        },
        modifier = modifier,
        update = { mapView ->
            mapViewRef = mapView
        }
    )
}

@Composable
private fun ShowMapLoadingProgressBar(
    visible: Boolean = false
) {
    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = visible,
        enter = EnterTransition.None,
        exit = fadeOut(),
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .wrapContentSize()
        )
    }
}

// Extension function to convert Compose Color to UIColor
private fun Color.toUIColor(): UIColor {
    val argb = this.toArgb()
    val alpha = ((argb shr 24) and 0xFF) / 255.0
    val red = ((argb shr 16) and 0xFF) / 255.0
    val green = ((argb shr 8) and 0xFF) / 255.0
    val blue = (argb and 0xFF) / 255.0
    return UIColor.colorWithRed(red, green, blue, alpha)
}

// Extension function to convert NSData to ByteArray
@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    val bytes = ByteArray(length)
    if (length > 0) {
        bytes.usePinned { pinned ->
            memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
    }
    return bytes
}