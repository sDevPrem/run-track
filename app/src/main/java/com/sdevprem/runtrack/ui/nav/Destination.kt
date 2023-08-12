package com.sdevprem.runtrack.ui.nav

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.navDeepLink
import com.sdevprem.runtrack.R

sealed class Destination(val route: String) {

    sealed class BottomNavDestination(
        route: String,
        @DrawableRes
        val icon: Int
    ) : Destination(route) {
        @Composable
        fun getIconVector() = ImageVector.vectorResource(icon)

        object Home : BottomNavDestination(route = "home", icon = R.drawable.ic_menu) {
            fun navigateToOnBoardingScreen(navController: NavController) {
                navController.navigate(OnBoardingDestination.route)
            }
        }

        object Profile : BottomNavDestination(route = "profile", icon = R.drawable.ic_profile)

    }

    object OnBoardingDestination : Destination("on_boarding") {
        fun navigateToHome(navController: NavController) {
            navController.navigate(BottomNavDestination.Home.route) {
                popUpTo(route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    object CurrentRun : Destination("current_run") {
        val currentRunUriPattern = "https://runtrack.sdevprem.com/$route"
        val deepLinks = listOf(
            navDeepLink {
                uriPattern = currentRunUriPattern
            }
        )
    }

    companion object {
        fun navigateToCurrentRunScreen(navController: NavController) {
            navController.navigate(CurrentRun.route)
        }
    }

}
