package com.sdevprem.runtrack.ui.screen.currentrun.component

import android.graphics.Bitmap
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastForEach
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.core.tracking.model.PathPoint
import com.sdevprem.runtrack.core.tracking.model.firstLocationPoint
import com.sdevprem.runtrack.core.tracking.model.lasLocationPoint
import com.sdevprem.runtrack.ui.common.utils.GoogleMapUtils
import com.sdevprem.runtrack.ui.theme.md_theme_light_primary

@Composable
fun Map(
    modifier: Modifier = Modifier,
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    onSnapshot: (Bitmap) -> Unit,
) {
    var mapSize by remember { mutableStateOf(Size(0f, 0f)) }
    var mapCenter by remember { mutableStateOf(Offset(0f, 0f)) }
    var isMapLoaded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                val rect = it.boundsInRoot()
                mapSize = rect.size
                mapCenter = rect.center
            }
    ) {
        ShowMapLoadingProgressBar(!isMapLoaded)
        Map(
            pathPoints = pathPoints,
            isRunningFinished = isRunningFinished,
            mapCenter = mapCenter,
            mapSize = mapSize,
            onMapLoaded = { isMapLoaded = true },
            onSnapshot = onSnapshot
        )
    }
}

@Composable
private fun Map(
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    mapCenter: Offset,
    mapSize: Size,
    onMapLoaded: () -> Unit,
    onSnapshot: (Bitmap) -> Unit,
) {

    val mapUiSettings = remember {
        MapUiSettings(
            mapToolbarEnabled = false,
            compassEnabled = true,
            zoomControlsEnabled = false
        )
    }
    val cameraPositionState = rememberCameraPositionState {}
    val lastLocationPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.lasLocationPoint() }
    }

    LaunchedEffect(key1 = lastLocationPoint) {
        lastLocationPoint?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(it.latLng, 15f)
                )
            )
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        uiSettings = mapUiSettings,
        cameraPositionState = cameraPositionState,
        onMapLoaded = onMapLoaded,
    ) {

        DrawPathPoints(pathPoints = pathPoints, isRunningFinished = isRunningFinished)

        TakeScreenShot(
            take = isRunningFinished,
            mapCenter = mapCenter,
            mapSize = mapSize,
            pathPoints = pathPoints,
            onSnapshot = onSnapshot
        )
    }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
private fun TakeScreenShot(
    take: Boolean,
    mapCenter: Offset,
    mapSize: Size,
    pathPoints: List<PathPoint>,
    onSnapshot: (Bitmap) -> Unit
) {
    MapEffect(key1 = take) { map ->
        if (take)
            GoogleMapUtils.takeSnapshot(
                map,
                pathPoints,
                mapCenter,
                onSnapshot,
                snapshotSideLength = mapSize.width / 2f
            )
    }
}

@Composable
@GoogleMapComposable
private fun DrawPathPoints(
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
) {
    val context = LocalContext.current
    val lastMarkerState = rememberMarkerState()
    val lastLocationPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.lasLocationPoint() }
    }
    val firstLocationPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.firstLocationPoint() }
    }

    LaunchedEffect(key1 = lastLocationPoint) {
        pathPoints.lasLocationPoint()?.let { lastMarkerState.position = it.latLng }
    }

    val latLngList = mutableListOf<LatLng>()
    pathPoints.fastForEach { pathPoint ->
        if (pathPoint is PathPoint.EmptyLocationPoint) {
            Polyline(
                points = latLngList.toList(),
                color = md_theme_light_primary,
            )
            latLngList.clear()
        } else if (pathPoint is PathPoint.LocationPoint) {
            latLngList += pathPoint.latLng
        }
    }

    //add the last path points
    if (latLngList.isNotEmpty())
        Polyline(
            points = latLngList.toList(),
            color = md_theme_light_primary
        )

    val currentPosIcon = remember {
        GoogleMapUtils.bitmapDescriptorFromVector(
            context = context,
            vectorResId = R.drawable.ic_circle,
            tint = md_theme_light_primary.toArgb()
        )
    }
    Marker(
        icon = currentPosIcon,
        state = lastMarkerState,
        anchor = Offset(0.5f, 0.5f),
        visible = lastLocationPoint != null
    )

    firstLocationPoint?.let {
        val firstLocationIcon = remember(isRunningFinished) {
            GoogleMapUtils.bitmapDescriptorFromVector(
                context = context,
                vectorResId = if (isRunningFinished) R.drawable.ic_circle else R.drawable.ic_circle_hollow,
                tint = if (isRunningFinished) md_theme_light_primary.toArgb() else null
            )
        }
        Marker(
            icon = firstLocationIcon,
            state = rememberMarkerState(position = it.latLng),
            anchor = Offset(0.5f, 0.5f)
        )
    }
}

@Composable
private fun ShowMapLoadingProgressBar(
    visible: Boolean = false
) {
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize(),
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