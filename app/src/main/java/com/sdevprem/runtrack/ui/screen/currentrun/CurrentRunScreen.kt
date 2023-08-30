package com.sdevprem.runtrack.ui.screen.currentrun

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
import com.sdevprem.runtrack.core.tracking.location.LocationUtils
import com.sdevprem.runtrack.core.tracking.model.CurrentRunState
import com.sdevprem.runtrack.core.tracking.model.PathPoint
import com.sdevprem.runtrack.domain.model.CurrentRunStateWithCalories
import com.sdevprem.runtrack.ui.theme.AppTheme
import com.sdevprem.runtrack.ui.theme.md_theme_light_primary
import com.sdevprem.runtrack.ui.utils.ComposeUtils
import com.sdevprem.runtrack.ui.utils.bitmapDescriptorFromVector
import com.sdevprem.runtrack.ui.utils.component.RunningStatsItem
import com.sdevprem.runtrack.utils.RunUtils
import com.sdevprem.runtrack.utils.RunUtils.firstLocationPoint
import com.sdevprem.runtrack.utils.RunUtils.lasLocationPoint
import com.sdevprem.runtrack.utils.RunUtils.takeSnapshot
import kotlinx.coroutines.delay
import java.math.RoundingMode

@Composable
@Preview(showBackground = true)
private fun CurrentRunComposable() {
    AppTheme {
        Surface {
            CurrentRunScreen(rememberNavController())
        }
    }
}

@Composable
fun CurrentRunScreen(
    navController: NavController,
    viewModel: CurrentRunViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        LocationUtils.checkAndRequestLocationSetting(context as Activity)
    }
    var isRunningFinished by rememberSaveable { mutableStateOf(false) }
    var shouldShowRunningCard by rememberSaveable { mutableStateOf(false) }
    val runState by viewModel.currentRunStateWithCalories.collectAsStateWithLifecycle()
    val runningDurationInMillis by viewModel.runningDurationInMillis.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        delay(ComposeUtils.slideDownInDuration + 200L)
        shouldShowRunningCard = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Map(
            pathPoints = runState.currentRunState.pathPoints,
            isRunningFinished = isRunningFinished,
        ) {
            viewModel.finishRun(it)
            navController.navigateUp()
        }
        TopBar(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            navController.navigateUp()
        }
        ComposeUtils.SlideDownAnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = shouldShowRunningCard
        ) {
            RunningCard(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                onPlayPauseButtonClick = viewModel::playPauseTracking,
                runState = runState,
                durationInMillis = runningDurationInMillis,
                onFinish = { isRunningFinished = true }
            )
        }

    }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
private fun BoxScope.Map(
    modifier: Modifier = Modifier,
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    onSnapshot: (Bitmap) -> Unit,
) {
    var mapSize by remember { mutableStateOf(Size(0f, 0f)) }
    var mapCenter by remember { mutableStateOf(Offset(0f, 0f)) }
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {}
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                mapToolbarEnabled = false,
                compassEnabled = true,
                zoomControlsEnabled = false
            )
        )
    }

    LaunchedEffect(key1 = pathPoints.lasLocationPoint()) {
        pathPoints.lasLocationPoint()?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(it.latLng, 15f)
                )
            )
        }

    }
    ShowMapLoadingProgressBar(isMapLoaded)
    GoogleMap(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                mapSize = size
                mapCenter = center
            },
        uiSettings = mapUiSettings,
        cameraPositionState = cameraPositionState,
        onMapLoaded = { isMapLoaded = true },

        ) {

        DrawPathPoints(pathPoints = pathPoints, isRunningFinished = isRunningFinished)

        MapEffect(key1 = isRunningFinished) { map ->
            if (isRunningFinished)
                takeSnapshot(
                    map,
                    pathPoints,
                    mapCenter,
                    onSnapshot,
                    snapshotSideLength = mapSize.width / 2
                )
        }
    }
}

@Composable
@GoogleMapComposable
private fun DrawPathPoints(
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
) {
    val lastMarkerState = rememberMarkerState()
    val lastLocationPoint = pathPoints.lasLocationPoint()
    lastLocationPoint?.let { lastMarkerState.position = it.latLng }

    val firstLocationPoint = pathPoints.firstLocationPoint()
    val firstPoint = remember(key1 = firstLocationPoint) { firstLocationPoint }

    val latLngList = mutableListOf<LatLng>()

    pathPoints.forEach { pathPoint ->
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

    val infiniteTransition = rememberInfiniteTransition()
    val lastMarkerPointColor by infiniteTransition.animateColor(
        initialValue = md_theme_light_primary,
        targetValue = md_theme_light_primary.copy(alpha = 0.8f),
        animationSpec = infiniteRepeatable(
            tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Marker(
        icon = bitmapDescriptorFromVector(
            context = LocalContext.current,
            vectorResId = R.drawable.ic_circle,
            tint = (if (isRunningFinished) md_theme_light_primary else lastMarkerPointColor).toArgb()
        ),
        state = lastMarkerState,
        anchor = Offset(0.5f, 0.5f),
        visible = lastLocationPoint != null
    )

    firstPoint?.let {
        Marker(
            icon = bitmapDescriptorFromVector(
                context = LocalContext.current,
                vectorResId = if (isRunningFinished) R.drawable.ic_circle else R.drawable.ic_circle_hollow,
                tint = if (isRunningFinished) md_theme_light_primary.toArgb() else null
            ),
            state = rememberMarkerState(position = it.latLng),
            anchor = Offset(0.5f, 0.5f)
        )
    }
}


@Composable
private fun BoxScope.ShowMapLoadingProgressBar(
    isMapLoaded: Boolean = false
) {
    AnimatedVisibility(
        modifier = Modifier
            .matchParentSize(),
        visible = !isMapLoaded,
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

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onUpButtonClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onUpButtonClick,
            modifier = Modifier
                .size(32.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = true
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                )
                .padding(4.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                contentDescription = "",
                modifier = Modifier,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RunningCard(
    modifier: Modifier = Modifier,
    durationInMillis: Long = 0L,
    runState: CurrentRunStateWithCalories,
    onPlayPauseButtonClick: () -> Unit = {},
    onFinish: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        RunningCardTime(
            modifier = Modifier
                .padding(
                    top = 24.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 16.dp
                )
                .fillMaxWidth(),
            durationInMillis = durationInMillis,
            isRunning = runState.currentRunState.isTracking,
            onPlayPauseButtonClick = onPlayPauseButtonClick,
            onFinish = onFinish
        )

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)

        ) {
            RunningStatsItem(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.running_boy),
                unit = "km",
                value = (runState.currentRunState.distanceInMeters / 1000f).toString()
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterVertically)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
            )
            RunningStatsItem(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.fire),
                unit = "kcal",
                value = runState.caloriesBurnt.toString()
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterVertically)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
            )
            RunningStatsItem(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.bolt),
                unit = "km/hr",
                value = runState.currentRunState.speedInKMH.toString()
            )
        }

    }
}

@Composable
private fun RunningCardTime(
    modifier: Modifier = Modifier,
    durationInMillis: Long,
    isRunning: Boolean,
    onPlayPauseButtonClick: () -> Unit,
    onFinish: () -> Unit
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Running Time",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = RunUtils.getFormattedStopwatchTime(durationInMillis),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
        if (!isRunning && durationInMillis > 0) {
            IconButton(
                onClick = onFinish,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.medium
                    )
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.ic_finish
                    ),
                    contentDescription = "",
                    modifier = Modifier
                        .size(16.dp),
                    tint = MaterialTheme.colorScheme.onError
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
        IconButton(
            onClick = onPlayPauseButtonClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = if (isRunning) R.drawable.ic_pause else R.drawable.ic_play
                ),
                contentDescription = "",
                modifier = Modifier
                    .size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun RunningCardPreview() {
    var isRunning by rememberSaveable { mutableStateOf(false) }
    RunningCard(
        durationInMillis = 5400000,
        runState = CurrentRunStateWithCalories(
            currentRunState = CurrentRunState(
                distanceInMeters = 600,
                speedInKMH = (6.935 /* m/s */ * 3.6).toBigDecimal()
                    .setScale(2, RoundingMode.HALF_UP)
                    .toFloat(),
                isTracking = isRunning
            ),
            caloriesBurnt = 532
        ),
        onPlayPauseButtonClick = {
            isRunning = !isRunning
        },
        onFinish = {}
    )
}