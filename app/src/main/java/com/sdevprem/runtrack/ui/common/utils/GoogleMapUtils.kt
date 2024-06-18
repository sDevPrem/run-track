package com.sdevprem.runtrack.ui.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.sdevprem.runtrack.core.tracking.model.PathPoint


object GoogleMapUtils {

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

    fun bitmapDescriptorFromVector(
        context: Context,
        vectorResId: Int,
        tint: Int? = null
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
        tint?.let { vectorDrawable.setTint(it) }

        vectorDrawable.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}