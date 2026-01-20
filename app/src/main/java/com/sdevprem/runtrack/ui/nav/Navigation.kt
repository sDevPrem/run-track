package com.sdevprem.runtrack.ui.nav

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sdevprem.runtrack.shared.ui.common.LocalVMProvider
import com.sdevprem.runtrack.shared.ui.screen.currentrun.CurrentRunScreen
import com.sdevprem.runtrack.shared.ui.screen.onboard.OnBoardScreen
import com.sdevprem.runtrack.shared.ui.screen.profile.ProfileScreen
import com.sdevprem.runtrack.shared.ui.screen.runstats.RunStatsScreen
import com.sdevprem.runtrack.ui.di.ViewModelProvider
import com.sdevprem.runtrack.ui.nav.Destination.CurrentRun

@Composable
fun Navigation(
    navController: NavHostController,
) {
    CompositionLocalProvider(
        LocalVMProvider provides ViewModelProvider
    ) {
        SetupNavGraph(
            navController = navController,
        )
    }
}

@Composable
private fun SetupNavGraph(
    navController: NavHostController,
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
            val context = LocalContext.current
            OnBoardScreen(
                navigateToHome = { Destination.OnBoardingDestination.navigateToHome(navController) },
                exitApp = { (context as? Activity)?.finish() }
            )
        }

        composable(route = Destination.RunStats.route) {
            RunStatsScreen(
                navigateUp = { navController.navigateUp() }
            )
        }
    }

}