package com.byme.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.byme.app.ui.auth.LoginScreen
import com.byme.app.ui.auth.RegisterScreen
import com.byme.app.ui.auth.SplashScreen
import com.byme.app.ui.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        composable(
            NavRoutes.SPLASH,
            exitTransition = {
                fadeOut(animationSpec = tween (500))
            }
        ) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.REGISTER)
                }

            )
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.HOME) {
            HomeScreen (
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.LOGIN)
                },
                onNavigateToProfessionalDetail = { professionalId ->
                    navController.navigate("professional_detail/$professionalId")
                }
            )
        }
        composable(NavRoutes.SEARCH_RESULTS) {
            // SearchResultsScreen()
        }
        composable(NavRoutes.PROFESSIONAL_DETAIL) { backStackEntry ->
            val professionalId = backStackEntry.arguments?.getString("professionalId")
            // ProfessionalDetailScreen(professionalId)
        }
        composable(NavRoutes.CHAT_LIST) {
            // ChatListScreen()
        }
        composable(NavRoutes.CHAT_DETAIL) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId")
            // ChatDetailScreen(chatId)
        }
        composable(NavRoutes.CALENDAR) {
            // CalendarScreen()
        }
        composable(NavRoutes.USER_PROFILE) {
            // UserProfileScreen()
        }
        composable(NavRoutes.PROFESSIONAL_PROFILE) {
            // ProfessionalProfileScreen()
        }
        composable(NavRoutes.OFFER_SERVICE) {
            // OfferServiceScreen()
        }
        composable(NavRoutes.ABOUT) {
            // AboutScreen()
        }
    }
}