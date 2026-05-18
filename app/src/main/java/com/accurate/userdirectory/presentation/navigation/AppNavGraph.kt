package com.accurate.userdirectory.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.accurate.userdirectory.core.designsystem.AccurateColors
import com.accurate.userdirectory.presentation.activity.ActivityScreen
import com.accurate.userdirectory.presentation.adduser.AddUserScreen
import com.accurate.userdirectory.presentation.settings.SettingsScreen
import com.accurate.userdirectory.presentation.splash.SplashScreen
import com.accurate.userdirectory.presentation.users.UserListScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem.Users,
        BottomNavItem.Activity,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }
    val showFab = currentDestination?.route == AppRoute.UserList.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = AccurateColors.Surface,
                    contentColor = AccurateColors.PrimaryPink
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AccurateColors.PrimaryPink,
                                selectedTextColor = AccurateColors.PrimaryPink,
                                unselectedIconColor = AccurateColors.TextTertiary,
                                unselectedTextColor = AccurateColors.TextTertiary,
                                indicatorColor = AccurateColors.PrimaryPinkLight.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = { navController.navigate(AppRoute.AddUser.createRoute()) },
                    containerColor = AccurateColors.PrimaryPink,
                    contentColor = AccurateColors.Surface
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah User")
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppRoute.Splash.route) {
                SplashScreen(
                    onNavigateToUserList = {
                        navController.navigate(AppRoute.UserList.route) {
                            popUpTo(AppRoute.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(AppRoute.UserList.route) {
                UserListScreen(
                    onNavigateToAddUser = { navController.navigate(AppRoute.AddUser.createRoute()) },
                    onNavigateToEditUser = { userId ->
                        navController.navigate(AppRoute.AddUser.createRoute(userId))
                    }
                )
            }
            composable(
                route = "add_user?userId={userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) {
                AddUserScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(AppRoute.Activity.route) {
                ActivityScreen()
            }
            composable(AppRoute.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
