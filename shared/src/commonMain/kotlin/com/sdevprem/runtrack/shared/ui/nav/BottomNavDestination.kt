package com.sdevprem.runtrack.shared.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import runtrack.shared.generated.resources.Res
import runtrack.shared.generated.resources.ic_menu
import runtrack.shared.generated.resources.ic_profile

sealed class BottomNavDestination(
    route: String,
    val icon: DrawableResource
) : Destination(route) {

    @Composable
    fun getIconVector() = vectorResource(icon)

    object Home : BottomNavDestination(route = "home", icon = Res.drawable.ic_menu) {

        fun navigateToOnBoardingScreen(navController: NavController) {
            navController.navigate(OnBoardingDestination.route)
        }

        fun navigateToRunStats(navController: NavController) {
            navController.navigate(RunStats.route)
        }

        object RecentRun : Destination("recent_run") {
            fun navigateToRunningHistoryScreen(navController: NavController) {
                navController.navigate(RunningHistory.route)
            }
        }

        object RunningHistory : Destination("running_history")

    }

    object Profile : BottomNavDestination(route = "profile", icon = Res.drawable.ic_profile)

}