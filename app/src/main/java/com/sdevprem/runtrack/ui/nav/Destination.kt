package com.sdevprem.runtrack.ui.nav

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.sdevprem.runtrack.R

sealed class Destination(val route: String) {
    sealed class BottomNavDestination(
        route: String,
        @DrawableRes
        val icon: Int
    ) : Destination(route) {

        object Home : BottomNavDestination(route = "home", icon = R.drawable.ic_menu)
        object Profile : BottomNavDestination(route = "profile", icon = R.drawable.ic_profile)

        @Composable
        fun getIconVector() = ImageVector.vectorResource(icon)

    }

    object CurrentRun : Destination("current_run")
}
