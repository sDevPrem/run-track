package com.sdevprem.runtrack.ui.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sdevprem.runtrack.ui.screen.home.HomeScreen
import com.sdevprem.runtrack.ui.screen.runninghistory.RunningHistoryScreen

fun NavGraphBuilder.homeNavigation(
    navController: NavController,
    paddingValues: PaddingValues
) {
    navigation(
        startDestination = BottomNavDestination.Home.RecentRun.route,
        route = BottomNavDestination.Home.route
    ) {
        composable(
            route = BottomNavDestination.Home.RecentRun.route
        ) {
            HomeScreen(
                navController = navController,
                bottomPadding = paddingValues.calculateBottomPadding()
            )
        }

        composable(
            route = BottomNavDestination.Home.RunningHistory.route
        ) {
            RunningHistoryScreen(
                paddingValues = paddingValues,
                navController = navController
            )
        }

    }
}