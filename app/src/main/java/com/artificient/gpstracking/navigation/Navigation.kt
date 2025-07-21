package com.artificient.gpstracking.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.artificient.gpstracking.R
import com.artificient.gpstracking.ui.screens.SettingsScreen
import com.artificient.gpstracking.ui.screens.TrackingScreen
import com.artificient.gpstracking.ui.screens.TripHistoryScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

sealed class Screen(val route: String, val titleResId: Int, val icon: @Composable () -> Unit) {
    object Tracking : Screen(
        route = "tracking",
        titleResId = R.string.title_tracking,
        icon = { Icon(Icons.Filled.LocationOn, contentDescription = null) }
    )
    
    object TripHistory : Screen(
        route = "trip_history",
        titleResId = R.string.title_trip_history,
        icon = { Icon(Icons.Filled.History, contentDescription = null) }
    )
    
    object Settings : Screen(
        route = "settings",
        titleResId = R.string.title_settings,
        icon = { Icon(Icons.Filled.Settings, contentDescription = null) }
    )
}

val screens = listOf(
    Screen.Tracking,
    Screen.TripHistory,
    Screen.Settings
)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = { BottomNavigation(navController = navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Tracking.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Tracking.route) {
                    TrackingScreen()
                }
                composable(Screen.TripHistory.route) {
                    TripHistoryScreen()
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = screen.icon,
                label = { Text(stringResource(screen.titleResId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
} 