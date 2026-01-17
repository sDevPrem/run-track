package com.sdevprem.runtrack.ui.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sdevprem.runtrack.shared.ui.common.LocalVMProvider
import com.sdevprem.runtrack.shared.ui.screen.runninghistory.RunningHistoryScreen
import com.sdevprem.runtrack.shared.ui.screen.runninghistory.RunningHistoryVM
import com.sdevprem.runtrack.ui.screen.home.HomeScreen

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
                navController = navController,
            )
        }

        composable(
            route = BottomNavDestination.Home.RunningHistory.route
        ) {
            RunningHistoryScreen(
                navigateUp = { navController.navigateUp() },
                viewModel = LocalVMProvider.current.provideViewModel(RunningHistoryVM::class)
            )
        }

    }
}