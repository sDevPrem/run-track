package com.sdevprem.runtrack.ui.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.sdevprem.runtrack.common.extension.toLatLng
import com.sdevprem.runtrack.domain.tracking.model.PathPoint
import kotlinx.coroutines.delay


object GoogleMapUtils {

    private const val MAP_SNAPSHOT_DELAY = 500L

    suspend fun takeSnapshot(
        map: GoogleMap,
        pathPoints: List<PathPoint>,
        mapCenter: Offset,
        onSnapshot: (Bitmap) -> Unit,
        snapshotSideLength: Float
    ) {
        val boundsBuilder = LatLngBounds.Builder()
        pathPoints.forEach {
            if (it is PathPoint.LocationPoint)
                boundsBuilder.include(it.locationInfo.toLatLng())
        }
        map.moveCamera(
            CameraUpdateFactory
                .newLatLngBounds(
                    boundsBuilder.build(),
                    snapshotSideLength.toInt(),
                    snapshotSideLength.toInt(),
                    (snapshotSideLength * 0.2).toInt()
                )
        )

        //since move camera bounds the map in the specified LocationInfo
        //from the center withing the bounding box (of side snapshotSideLength)
        //so get the coordinate of the starting point of the box
        val startOffset = mapCenter - Offset(snapshotSideLength / 2, snapshotSideLength / 2)

        //A delay to load the icons and map properly before snapshot
        delay(MAP_SNAPSHOT_DELAY)
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

    fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorResId: Int,
        tint: Int? = null,
        sizeInPx: Int? = null,
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
        tint?.let { vectorDrawable.setTint(it) }

        vectorDrawable.setBounds(
            0,
            0,
            sizeInPx ?: vectorDrawable.intrinsicWidth,
            sizeInPx ?: vectorDrawable.intrinsicHeight
        )

        val bitmap = Bitmap.createBitmap(
            sizeInPx ?: vectorDrawable.intrinsicWidth,
            sizeInPx ?: vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}