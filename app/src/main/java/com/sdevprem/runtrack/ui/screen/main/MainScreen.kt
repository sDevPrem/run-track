package com.sdevprem.runtrack.ui.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.ui.common.compose.animation.ComposeUtils
import com.sdevprem.runtrack.ui.common.compose.compositonLocal.LocalScaffoldBottomPadding
import com.sdevprem.runtrack.ui.common.extension.navigateToBottomNavDestination
import com.sdevprem.runtrack.ui.nav.BottomNavDestination
import com.sdevprem.runtrack.ui.nav.Destination
import com.sdevprem.runtrack.ui.nav.Navigation
import com.sdevprem.runtrack.ui.theme.AppTheme
import kotlinx.coroutines.delay

@Composable
@Preview(showBackground = true)
private fun MainScreenPreview() {
    AppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(rememberNavController())
        }
    }
}

@Composable
fun MainScreen(
    navHostController: NavHostController,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    var shouldShowBottomNav by rememberSaveable { mutableStateOf(false) }
    var shouldShowFAB by rememberSaveable { mutableStateOf(false) }
    var hideBottomItems by rememberSaveable { mutableStateOf(true) }
    val doesUserExist by viewModel.doesUserExist.collectAsStateWithLifecycle()

    hideBottomItems = when (navBackStackEntry?.destination?.route) {
        Destination.CurrentRun.route -> true
        Destination.OnBoardingDestination.route -> true
        Destination.RunStats.route -> true
        else -> false
    }

    LaunchedEffect(hideBottomItems) {
        if (hideBottomItems) {
            shouldShowFAB = false
            delay(150)
            shouldShowBottomNav = false
        } else {
            shouldShowBottomNav = true
            delay(150)
            shouldShowFAB = true
        }
    }

    Scaffold(
        bottomBar = {
            ComposeUtils.SlideUpAnimatedVisibility(
                visible = shouldShowBottomNav && doesUserExist == true,
            ) {
                BottomBar(navController = navHostController)
            }
        },
        floatingActionButton = {
            ShrinkAnimatedVisibility(visible = shouldShowFAB && doesUserExist == true) {
                FloatingActionButton(
                    onClick = {
                        navHostController.navigate(
                            Destination.CurrentRun.route
                        )
                    },
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    backgroundColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_run),
                        contentDescription = "",
                    )

                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CompositionLocalProvider(
                LocalScaffoldBottomPadding provides it.calculateBottomPadding()
            ) {
                Navigation(navHostController)
            }
        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ShrinkAnimatedVisibility(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = tween(150)
        ),
        exit = scaleOut(
            animationSpec = tween(150)
        ),
        content = content
    )
}

@Composable
private fun BottomBar(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.primary,
                spotColor = MaterialTheme.colorScheme.primary,
                clip = true
            )
    ) {
        BottomNavBar(
            navController = navController,
            items = listOf(
                BottomNavDestination.Home,
                BottomNavDestination.Profile
            ),
            modifier = Modifier
        )
    }
}

@Composable
private fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    items: List<BottomNavDestination>
) {
    BottomNavigation(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.surface,
        elevation = 0.dp,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach {
            BottomNavItem(it, navController, currentDestination)
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    item: BottomNavDestination,
    navController: NavController,
    currentDestination: NavDestination?
) {
    BottomNavigationItem(
        icon = {
            Icon(imageVector = item.getIconVector(), contentDescription = "")
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == item.route
        } == true,
        onClick = { navController.navigateToBottomNavDestination(item) },
        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        selectedContentColor = MaterialTheme.colorScheme.primary,
    )
}
