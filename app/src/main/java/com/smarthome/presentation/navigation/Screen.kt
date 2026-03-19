package com.smarthome.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object InfoCollection : Screen("info_collection")
    data object HouseLayout : Screen("house_layout")
    data object Budget : Screen("budget")
    data object Generating : Screen("generating")
    data object SchemeDetail : Screen("scheme/{schemeId}") {
        fun createRoute(schemeId: String) = "scheme/$schemeId"
    }
    data object MySchemes : Screen("my_schemes")
    data object Profile : Screen("profile")
    data object DeviceDetail : Screen("device/{deviceId}") {
        fun createRoute(deviceId: String) = "device/$deviceId"
    }
    data object Feedback : Screen("feedback")
}
