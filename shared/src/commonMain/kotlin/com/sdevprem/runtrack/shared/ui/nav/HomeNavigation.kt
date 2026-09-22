package com.sdevprem.runtrack.shared.ui.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sdevprem.runtrack.shared.ui.screen.home.HomeScreen
import com.sdevprem.runtrack.shared.ui.screen.runninghistory.RunningHistoryScreen

fun NavGraphBuilder.homeNavigation(
    navController: NavController,
) {
    navigation(
        startDestination = BottomNavDestination.Home.RecentRun.route,
        route = BottomNavDestination.Home.route
    ) {
        composable(
            route = BottomNavDestination.Home.RecentRun.route
        ) {
            HomeScreen(
                navigateToRunScreen = {
                    Destination.navigateToCurrentRunScreen(navController)
                },
                navigateToRunningHistoryScreen = {
                    BottomNavDestination.Home.RecentRun.navigateToRunningHistoryScreen(navController)
                },
                navigateToRunStats = {
                    BottomNavDestination.Home.navigateToRunStats(navController)
                },
                navigateToOnBoardingScreen = {
                    BottomNavDestination.Home
                        .navigateToOnBoardingScreen(navController)
                }
            )
        }

        composable(
            route = BottomNavDestination.Home.RunningHistory.route
        ) {
            RunningHistoryScreen(
                navigateUp = { navController.navigateUp() },
            )
        }
    }
}
    