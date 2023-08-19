package com.sdevprem.runtrack.ui.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sdevprem.runtrack.ui.nav.Destination.BottomNavDestination
import com.sdevprem.runtrack.ui.nav.Destination.CurrentRun
import com.sdevprem.runtrack.ui.screen.currentrun.CurrentRunScreen
import com.sdevprem.runtrack.ui.screen.onboard.OnBoardScreen
import com.sdevprem.runtrack.ui.screen.profile.ProfileScreen

@Composable
fun Navigation(
    navController: NavHostController,
    paddingValues: PaddingValues = PaddingValues()
) {
    SetupNavGraph(
        navController = navController,
        paddingValues = paddingValues
    )
}

@Composable
private fun SetupNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues = PaddingValues()
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavDestination.Home.route
    ) {
        homeNavigation(navController, paddingValues)

        composable(
            route = BottomNavDestination.Profile.route
        ) {
            ProfileScreen(paddingValues.calculateBottomPadding())
        }

        composable(
            route = CurrentRun.route,
            deepLinks = CurrentRun.deepLinks
        ) {
            CurrentRunScreen(navController)
        }

        composable(
            route = Destination.OnBoardingDestination.route
        ) {
            OnBoardScreen(navController = navController)
        }
    }

}