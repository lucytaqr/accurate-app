package com.accurate.userdirectory.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Users : BottomNavItem(AppRoute.UserList.route, "Users", Icons.Default.Groups)
    data object Activity : BottomNavItem(AppRoute.Activity.route, "Activity", Icons.Default.ListAlt)
    data object Settings : BottomNavItem(AppRoute.Settings.route, "Settings", Icons.Default.Settings)
}
