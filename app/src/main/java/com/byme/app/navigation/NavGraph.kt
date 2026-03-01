package com.byme.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        composable(NavRoutes.SPLASH) {
            // SplashScreen()
        }
        composable(NavRoutes.LOGIN) {
            // LoginScreen()
        }
        composable(NavRoutes.REGISTER) {
            // RegisterScreen()
        }
        composable(NavRoutes.HOME) {
            // HomeScreen()
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