package com.sdevprem.runtrack.shared.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sdevprem.runtrack.shared.ui.nav.Destination.CurrentRun
import com.sdevprem.runtrack.shared.ui.screen.currentrun.CurrentRunScreen
import com.sdevprem.runtrack.shared.ui.screen.onboard.OnBoardScreen
import com.sdevprem.runtrack.shared.ui.screen.profile.ProfileScreen
import com.sdevprem.runtrack.shared.ui.screen.runstats.RunStatsScreen

@Composable
fun Navigation(
    navController: NavHostController,
    exitApp: () -> Unit,
) {
    SetupNavGraph(
        navController = navController,
        exitApp = exitApp
    )
}

@Composable
private fun SetupNavGraph(
    navController: NavHostController,
    exitApp: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavDestination.Home.route
    ) {
        homeNavigation(navController)

        composable(
            route = BottomNavDestination.Profile.route
        ) {
            ProfileScreen()
        }

        composable(
            route = CurrentRun.route,
            deepLinks = CurrentRun.deepLinks
        ) {
            CurrentRunScreen(
                navigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = Destination.OnBoardingDestination.route
        ) {
            OnBoardScreen(
                navigateToHome = { Destination.OnBoardingDestination.navigateToHome(navController) },
                exitApp =  exitApp
            )
        }

        composable(route = Destination.RunStats.route) {
            RunStatsScreen(
                navigateUp = { navController.navigateUp() }
            )
        }
    }

}