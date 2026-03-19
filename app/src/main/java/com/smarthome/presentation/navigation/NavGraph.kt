package com.smarthome.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.smarthome.presentation.device.DeviceDetailScreen
import com.smarthome.presentation.feedback.FeedbackScreen
import com.smarthome.presentation.home.HomeScreen
import com.smarthome.presentation.info.HouseLayoutScreen
import com.smarthome.presentation.info.InfoCollectionScreen
import com.smarthome.presentation.info.BudgetScreen
import com.smarthome.presentation.login.LoginScreen
import com.smarthome.presentation.myschemes.MySchemesScreen
import com.smarthome.presentation.profile.ProfileScreen
import com.smarthome.presentation.scheme.GeneratingScreen
import com.smarthome.presentation.scheme.SchemeDetailScreen
import com.smarthome.presentation.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToInfoCollection = {
                    navController.navigate(Screen.InfoCollection.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToInfoCollection = {
                    navController.navigate(Screen.InfoCollection.route)
                },
                onNavigateToMySchemes = {
                    navController.navigate(Screen.MySchemes.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToSchemeDetail = { schemeId ->
                    navController.navigate(Screen.SchemeDetail.createRoute(schemeId))
                }
            )
        }

        composable(Screen.InfoCollection.route) {
            InfoCollectionScreen(
                onNavigateToHouseLayout = {
                    navController.navigate(Screen.HouseLayout.route)
                },
                onNavigateToGenerating = {
                    navController.navigate(Screen.Generating.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.HouseLayout.route) {
            HouseLayoutScreen(
                onNavigateToBudget = {
                    navController.navigate(Screen.Budget.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Budget.route) {
            BudgetScreen(
                onNavigateToGenerating = { budget ->
                    navController.navigate(Screen.Generating.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Generating.route) {
            GeneratingScreen(
                onNavigateToSchemeDetail = { schemeId ->
                    navController.navigate(Screen.SchemeDetail.createRoute(schemeId)) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.SchemeDetail.route,
            arguments = listOf(
                navArgument("schemeId") { type = NavType.StringType }
            )
        ) {
            SchemeDetailScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToDeviceDetail = { deviceId ->
                    navController.navigate(Screen.DeviceDetail.createRoute(deviceId))
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
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToFeedback = {
                    navController.navigate(Screen.Feedback.route)
                }
            )
        }

        composable(
            route = Screen.DeviceDetail.route,
            arguments = listOf(
                navArgument("deviceId") { type = NavType.StringType }
            )
        ) {
            DeviceDetailScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Feedback.route) {
            FeedbackScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
