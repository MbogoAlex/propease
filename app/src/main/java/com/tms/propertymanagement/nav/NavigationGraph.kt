package com.tms.propertymanagement.nav

import HomeScreen
import HomeScreenDestination
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tms.propertymanagement.ui.screens.SplashScreen
import com.tms.propertymanagement.ui.screens.SplashScreenDestination
import com.tms.propertymanagement.ui.screens.WelcomeScreen
import com.tms.propertymanagement.ui.screens.WelcomeScreenDestination
import com.tms.propertymanagement.ui.screens.accountManagement.LoginScreen
import com.tms.propertymanagement.ui.screens.accountManagement.LoginScreenDestination
import com.tms.propertymanagement.ui.screens.accountManagement.RegistrationScreen
import com.tms.propertymanagement.ui.screens.accountManagement.RegistrationScreenDestination
import com.tms.propertymanagement.ui.screens.appContentPages.ListingDetailsDestination
import com.tms.propertymanagement.ui.screens.appContentPages.ListingDetailsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SplashScreenDestination.route,
        modifier = modifier
    ) {
        composable(SplashScreenDestination.route) {
            SplashScreen(
                navigateToWelcomeScreen = {
                    navController.popBackStack(SplashScreenDestination.route, true)
                    navController.navigate(WelcomeScreenDestination.route)
                },
                navigateToHomeScreen = {
                    navController.popBackStack(SplashScreenDestination.route, true)
                    navController.navigate(HomeScreenDestination.route)
                }
            )
        }
        composable(WelcomeScreenDestination.route) {
            WelcomeScreen(
                navigateToRegistrationPage = {
                    navController.popBackStack(WelcomeScreenDestination.route, true)
                    navController.navigate(RegistrationScreenDestination.route)
                }
            )
        }
        composable(RegistrationScreenDestination.route) {
            RegistrationScreen(
                navigateToLoginScreen = {phoneNumber, password ->
                    navController.popBackStack(RegistrationScreenDestination.route, true)
                    navController.navigate("${LoginScreenDestination.route}/${phoneNumber}/${password}")
                },
                navigateToPreviousScreen = {
                    navController.navigate(RegistrationScreenDestination.route)
                }
            )
        }
        composable(
            LoginScreenDestination.routeArgs,
            arguments = listOf(
                navArgument(LoginScreenDestination.phoneNumber) {
                    type = NavType.StringType
                },
                navArgument(LoginScreenDestination.password) {
                    type = NavType.StringType
                }
            )
        ) {
            LoginScreen(
                navigateToPreviousScreen = {
                    navController.popBackStack(LoginScreenDestination.routeArgs, true)
                    navController.navigate(RegistrationScreenDestination.route)

                },
                navigateToRegistrationScreen = {
                    navController.popBackStack(LoginScreenDestination.routeArgs, true)
                    navController.navigate(RegistrationScreenDestination.route)
                },
                navigateToHomeScreen = {
                    navController.popBackStack(LoginScreenDestination.routeArgs, true)
                    navController.navigate(HomeScreenDestination.route)
                }
            )
        }
        composable(HomeScreenDestination.route) {
            HomeScreen(
                navigateToSpecificProperty = {
                    navController.navigate("${ListingDetailsDestination.route}/${it}")
                },
                navigateToHomeScreen = {
                    navController.popBackStack(HomeScreenDestination.route, true)
                    navController.navigate(HomeScreenDestination.route)
                }
            )
        }
        composable(
            ListingDetailsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ListingDetailsDestination.propertyId) {
                    type = NavType.StringType
                }
            )
        ) {
            ListingDetailsScreen(
                navigateToPreviousScreen = {
                    navController.popBackStack(ListingDetailsDestination.routeWithArgs, true)
                    navController.enableOnBackPressed(true)
                }
            )
        }
    }
}