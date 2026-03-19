package com.byme.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.byme.app.ui.about.AboutScreen
import com.byme.app.ui.auth.LoginScreen
import com.byme.app.ui.auth.RegisterScreen
import com.byme.app.ui.auth.SplashScreen
import com.byme.app.ui.home.HomeScreen
import com.byme.app.ui.professional.OfferServiceScreen
import com.byme.app.ui.professional.ProfessionalDetailScreen
import com.byme.app.ui.profile.ProfileScreen

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
                },
                onNavigateToProfile = { navController.navigate(NavRoutes.USER_PROFILE) },
                onNavigateToMessages = { navController.navigate(NavRoutes.CHAT_LIST) },
                onNavigateToCalendar = { navController.navigate(NavRoutes.CALENDAR) }
            )
        }
        composable(NavRoutes.SEARCH_RESULTS) {
            // SearchResultsScreen()
        }
        composable(
            route = NavRoutes.PROFESSIONAL_DETAIL,
            arguments = listOf(navArgument("professionalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val professionalId = backStackEntry.arguments?.getString("professionalId") ?: ""
            ProfessionalDetailScreen(
                professionalId = professionalId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigate(NavRoutes.LOGIN) },
                onNavigateToProfile = { navController.navigate(NavRoutes.USER_PROFILE) },
                onNavigateToMessages = { navController.navigate(NavRoutes.CHAT_LIST) },
                onNavigateToCalendar = { navController.navigate(NavRoutes.CALENDAR) },
                onNavigateToHome = { navController.navigate(NavRoutes.HOME) }
            )
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
            ProfileScreen(
            onNavigateToLogin = { navController.navigate(NavRoutes.LOGIN) },
            onNavigateToProfessionalProfile = { navController.navigate(NavRoutes.OFFER_SERVICE) },
            onNavigateToAbout = { navController.navigate(NavRoutes.ABOUT) },
            onNavigateToMessages = { navController.navigate(NavRoutes.CHAT_LIST) },
            onNavigateToCalendar = { navController.navigate(NavRoutes.CALENDAR) },
            onNavigateToHome = { navController.navigate(NavRoutes.HOME) }
        )
        }
        composable(NavRoutes.PROFESSIONAL_PROFILE) {
            // ProfessionalProfileScreen()
        }
        composable(NavRoutes.OFFER_SERVICE) {
            OfferServiceScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.OFFER_SERVICE) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.ABOUT) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}