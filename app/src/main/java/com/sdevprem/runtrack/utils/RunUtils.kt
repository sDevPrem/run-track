package com.sdevprem.runtrack.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.sdevprem.runtrack.core.tracking.model.PathPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object RunUtils {

    val locationPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).toTypedArray()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val notificationPermission = Manifest.permission.POST_NOTIFICATIONS

    val allPermissions = mutableListOf<String>().apply {
        addAll(locationPermissions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(notificationPermission)
        }
    }.toTypedArray()

    fun Context.hasNotificationPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                notificationPermission
            ) == PERMISSION_GRANTED
        } else true


    fun Context.hasLocationPermission() =
        locationPermissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PERMISSION_GRANTED
        }

    fun Context.hasAllPermission() =
        allPermissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PERMISSION_GRANTED
        }

    fun Context.openAppSetting() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also(::startActivity)
    }

    fun getFormattedStopwatchTime(ms: Long, includeMillis: Boolean = false): String {
        var milliseconds = ms
        val hrs = TimeUnit.MILLISECONDS.toHours(ms)
        milliseconds -= TimeUnit.HOURS.toMillis(hrs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        val formattedString =
            "${if (hrs < 10) "0" else ""}$hrs:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"

        return if (!includeMillis) {
            formattedString
        } else {
            milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
            milliseconds /= 10
            formattedString + ":" +
                    "${if (milliseconds < 10) "0" else ""}$milliseconds"
        }
    }

    fun getDistanceBetweenPathPoints(
        pathPoint1: PathPoint,
        pathPoint2: PathPoint
    ): Int {
        return if (pathPoint1 is PathPoint.LocationPoint && pathPoint2 is PathPoint.LocationPoint) {
            val result = FloatArray(1)
            Location.distanceBetween(
                pathPoint1.latLng.latitude,
                pathPoint1.latLng.longitude,
                pathPoint2.latLng.latitude,
                pathPoint2.latLng.longitude,
                result
            )
            result[0].roundToInt()
        } else 0
    }

    fun calculateDistanceCovered(pathPoints: List<PathPoint>): Int {
        var distance = 0
        pathPoints.forEachIndexed { i, pathPoint ->
            if (i == pathPoints.size - 1)
                return@forEachIndexed
            distance += getDistanceBetweenPathPoints(pathPoint, pathPoints[i + 1])
        }
        return distance
    }

    fun takeSnapshot(
        map: GoogleMap,
        pathPoints: List<PathPoint>,
        mapCenter: Offset,
        onSnapshot: (Bitmap) -> Unit,
        snapshotSideLength: Float
    ) {
        val boundsBuilder = LatLngBounds.Builder()
        pathPoints.forEach {
            if (it is PathPoint.LocationPoint)
                boundsBuilder.include(it.latLng)
        }
        map.moveCamera(
            CameraUpdateFactory
                .newLatLngBounds(
                    boundsBuilder.build(),
                    snapshotSideLength.toInt(),
                    snapshotSideLength.toInt(),
                    (snapshotSideLength * 0.05).toInt()
                )
        )

        //since move camera bounds the map in the specified LatLng
        //from the center withing the bounding box (of side snapshotSideLength)
        //so get the coordinate of the starting point of the box
        val startOffset = mapCenter - Offset(snapshotSideLength / 2, snapshotSideLength / 2)

        map.snapshot {
            it?.let {
                //crop to get a square image which fits the user path
                val croppedBitmap = Bitmap.createBitmap(
                    it,
                    startOffset.x.toInt(), //start x
                    startOffset.y.toInt(), //start y
                    snapshotSideLength.toInt(), //width
                    snapshotSideLength.toInt() //height
                )
                onSnapshot(croppedBitmap)
            }
        }
    }

    fun List<PathPoint>.lasLocationPoint(): PathPoint.LocationPoint? {
        for (i in lastIndex downTo 0)
            if (get(i) is PathPoint.LocationPoint)
                return get(i) as PathPoint.LocationPoint
        return null
    }

    fun List<PathPoint>.firstLocationPoint() =
        find { it is PathPoint.LocationPoint } as? PathPoint.LocationPoint


    fun Date.getDisplayDate(): String =
        SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            .format(this)

    fun calculateCaloriesBurnt(distanceInMeters: Int, weightInKg: Float) =
        //from chat gpt
        (0.75f * weightInKg) * (distanceInMeters / 1000f)


}