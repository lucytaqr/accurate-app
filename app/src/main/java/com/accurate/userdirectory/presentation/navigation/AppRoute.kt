package com.accurate.userdirectory.presentation.navigation

sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object UserList : AppRoute("user_list")
    data object AddUser : AppRoute("add_user")
    data object Activity : AppRoute("activity")
    data object Settings : AppRoute("settings")
}
