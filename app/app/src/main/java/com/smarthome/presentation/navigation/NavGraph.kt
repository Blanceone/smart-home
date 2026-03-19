package com.smarthome.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smarthome.presentation.screens.home.HomeScreen
import com.smarthome.presentation.screens.info.InfoCollectionScreen
import com.smarthome.presentation.screens.login.LoginScreen
import com.smarthome.presentation.screens.myschemes.MySchemesScreen
import com.smarthome.presentation.screens.profile.ProfileScreen
import com.smarthome.presentation.screens.scheme.SchemeDetailScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object InfoCollection : Screen("info_collection")
    object SchemeDetail : Screen("scheme_detail/{schemeId}") {
        fun createRoute(schemeId: String) = "scheme_detail/$schemeId"
    }
    object MySchemes : Screen("my_schemes")
    object Profile : Screen("profile")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToInfo = {
                    navController.navigate(Screen.InfoCollection.route)
                },
                onNavigateToScheme = { schemeId ->
                    navController.navigate(Screen.SchemeDetail.createRoute(schemeId))
                },
                onNavigateToMySchemes = {
                    navController.navigate(Screen.MySchemes.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.InfoCollection.route) {
            InfoCollectionScreen(
                onComplete = { schemeId ->
                    navController.navigate(Screen.SchemeDetail.createRoute(schemeId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.SchemeDetail.route) { backStackEntry ->
            val schemeId = backStackEntry.arguments?.getString("schemeId") ?: ""
            SchemeDetailScreen(
                schemeId = schemeId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MySchemes.route) {
            MySchemesScreen(
                onNavigateToScheme = { schemeId ->
                    navController.navigate(Screen.SchemeDetail.createRoute(schemeId))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
