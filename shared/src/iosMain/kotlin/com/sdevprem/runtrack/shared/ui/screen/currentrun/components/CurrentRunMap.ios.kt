package com.sdevprem.runtrack.shared.ui.screen.currentrun.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.Google_Maps_iOS_Utils.GMSCameraPosition
import cocoapods.Google_Maps_iOS_Utils.GMSMapView
import com.sdevprem.runtrack.shared.domain.tracking.model.PathPoint
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Map(
    modifier: Modifier,
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    onSnapshot: (ByteArray) -> Unit
) {
    val camera = remember {
        GMSCameraPosition.cameraWithLatitude(
            latitude = 28.4595,
            longitude = 77.0266,
            zoom = 12F,
        )
    }
    UIKitView(
        factory = {
            val mapView = GMSMapView.mapWithFrame(
                frame = platform.CoreGraphics.CGRectZero.readValue(),
                camera = camera
            ).apply {

            }
            mapView.settings().setCompassButton(true)

            // Return the view to Compose
            mapView
        },
        modifier = Modifier.fillMaxSize(),
    )
}