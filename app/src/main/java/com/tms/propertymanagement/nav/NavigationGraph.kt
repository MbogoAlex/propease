package com.propertymanagement.tms.nav

import HomeScreen
import HomeScreenDestination
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.propertymanagement.tms.ui.screens.SplashScreen
import com.propertymanagement.tms.ui.screens.SplashScreenDestination
import com.propertymanagement.tms.ui.screens.WelcomeScreen
import com.propertymanagement.tms.ui.screens.WelcomeScreenDestination
import com.propertymanagement.tms.ui.screens.accountManagement.LoginScreen
import com.propertymanagement.tms.ui.screens.accountManagement.LoginScreenDestination
import com.propertymanagement.tms.ui.screens.accountManagement.RegistrationScreen
import com.propertymanagement.tms.ui.screens.accountManagement.RegistrationScreenDestination
import com.propertymanagement.tms.ui.screens.appContentPages.ListingDetailsDestination
import com.propertymanagement.tms.ui.screens.appContentPages.ListingDetailsScreen
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.PropertyUpdateScreen
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.PropertyUpdateScreenDestination
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.UserLivePropertyDetailsScreen
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.UserLivePropertyDetailsScreenDestination
import com.tms.propertymanagement.ui.screens.accountManagement.ProfileUpdateScreen
import com.tms.propertymanagement.ui.screens.accountManagement.ProfileUpdateScreenDestination
import com.tms.propertymanagement.ui.screens.accountManagement.profileVerification.ProfileVerificationComposable
import kotlin.reflect.typeOf

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
                },
                navigateToHomeScreenWithoutArgs = {
                    navController.popBackStack(WelcomeScreenDestination.route, true)
                    navController.navigate(HomeScreenDestination.route)
                },
            )
        }
        composable(RegistrationScreenDestination.route) {
            RegistrationScreen(
                navigateToLoginScreenWithArguments = {phoneNumber, password ->

                    navController.popBackStack(RegistrationScreenDestination.route, true)
                    navController.navigate("${LoginScreenDestination.route}/${phoneNumber}/${password}")
                },
                navigateToPreviousScreen = {
                    navController.navigateUp()
                },
                navigateToLoginScreenWithoutArguments = {
                    navController.popBackStack(RegistrationScreenDestination.route, true)
                    navController.navigate(LoginScreenDestination.route)
                },
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
            Log.i("LOGIN_SCREEN_NAVIGATION_WITH_ARGS", "${LoginScreenDestination.phoneNumber} ${LoginScreenDestination.password}")
            LoginScreen(
                navigateToPreviousScreen = {
                    navController.popBackStack(LoginScreenDestination.routeArgs, true)
//                    navController.navigate(RegistrationScreenDestination.route)

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
        composable(LoginScreenDestination.route) {
            Log.i("LOGIN_SCREEN_NAVIGATION_WITHOUT_ARGS", "No args")
            LoginScreen(
                navigateToPreviousScreen = {
                                           navController.navigateUp()
//                    navController.popBackStack(LoginScreenDestination.route, true)
//                    navController.navigate(RegistrationScreenDestination.route)

                },
                navigateToRegistrationScreen = {
                    navController.popBackStack(LoginScreenDestination.route, true)
                    navController.navigate(RegistrationScreenDestination.route)
                },
                navigateToHomeScreen = {
                    navController.popBackStack(LoginScreenDestination.route, true)
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
                },
                navigateToSpecificUserProperty = {
                    navController.navigate("${UserLivePropertyDetailsScreenDestination.route}/${it}")
                },
                navigateToHomeScreenWithArguments = {
                    navController.popBackStack(HomeScreenDestination.route, true)
                    navController.navigate("${HomeScreenDestination.route}/${it}")
                },
                navigateToLoginScreenWithoutArgs = {
                    navController.popBackStack(HomeScreenDestination.route, true)
                    navController.navigate(LoginScreenDestination.route)
                },
                navigateToRegistrationScreen = {
                    navController.popBackStack(HomeScreenDestination.route, true)
                    navController.navigate(RegistrationScreenDestination.route)
                },
                navigateToUpdateProfileScreen = {
                    navController.navigate(ProfileUpdateScreenDestination.route)
                },
                navigateToLoginScreenWithArgs = {phoneNumber, password ->
                    navController.navigate("${LoginScreenDestination.route}/${phoneNumber}/${password}")
                },
                navigateToProfileVerificationScreen = {
                    navController.navigate(ProfileUpdateScreenDestination.route)
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
                    navController.popBackStack()
                }
            )
        }
        composable(
            UserLivePropertyDetailsScreenDestination.routeWithArgs,
            arguments = listOf(
                navArgument(UserLivePropertyDetailsScreenDestination.propertyId) {
                    type = NavType.StringType
                }
            )
        ) {
            UserLivePropertyDetailsScreen(
                navigateToPreviousScreen = {
                    navController.navigateUp()
                },
                navigateToPropertyUpdateScreen = {
                    navController.navigate("${PropertyUpdateScreenDestination.route}/${it}")
                },
                navigateToHomeScreenWithArgs = {
                    navController.popBackStack()
                    navController.navigate("${HomeScreenDestination.route}/${it}")
                }
            )
        }
        composable(
            PropertyUpdateScreenDestination.routeWithArgs,
            arguments = listOf(
                navArgument(PropertyUpdateScreenDestination.propertyId) {
                    type = NavType.StringType
                }
            )
        ) {
            PropertyUpdateScreen(
                navigateToPreviousScreen = {
//                    navController.popBackStack(UserLivePropertyDetailsScreenDestination.routeWithArgs, true)
                    navController.navigateUp()
                },
                navigateToHomeScreenWithArgs = {
                    navController.navigate("${HomeScreenDestination.route}/${it}")
                }
            )
        }
        composable(
            HomeScreenDestination.routeWithArgs,
            arguments = listOf(
                navArgument(HomeScreenDestination.childScreen) {
                    type = NavType.StringType
                }
            )
        ) {
            HomeScreen(
                navigateToSpecificProperty = {
                    navController.navigate("${ListingDetailsDestination.route}/${it}")
                },
                navigateToHomeScreen = {
                    navController.popBackStack(HomeScreenDestination.route, true)
                    navController.navigate(HomeScreenDestination.route)
                },
                navigateToSpecificUserProperty = {
                    navController.navigate("${UserLivePropertyDetailsScreenDestination.route}/${it}")
                },
                navigateToHomeScreenWithArguments = {
                    navController.popBackStack(HomeScreenDestination.routeWithArgs, true)
                    navController.navigate("${HomeScreenDestination.route}/${it}")
                },
                navigateToLoginScreenWithoutArgs = {
                    navController.popBackStack(HomeScreenDestination.routeWithArgs, true)
                    navController.navigate(LoginScreenDestination.route)
                },
                navigateToRegistrationScreen = {
                    navController.popBackStack(HomeScreenDestination.route, true)
                    navController.navigate(RegistrationScreenDestination.route)
                },
                navigateToUpdateProfileScreen = {
                    navController.navigate(ProfileUpdateScreenDestination.route)
                },
                navigateToLoginScreenWithArgs = {phoneNumber, password ->
                    navController.navigate("${LoginScreenDestination.route}/${phoneNumber}/${password}")
                },
                navigateToProfileVerificationScreen = {
                    navController.navigate(ProfileUpdateScreenDestination.route)
                }
            )
        }
        composable(ProfileUpdateScreenDestination.route) {
            ProfileUpdateScreen(
                navigateToHomeScreenWithArgs = {
                    navController.navigate("${HomeScreenDestination.route}/${it}")
                },
                navigateToPreviousScreen = {
                    navController.navigateUp()
                },
                navigateToLoginScreenWithArgs = {phoneNumber, password ->
                    navController.navigate("${LoginScreenDestination.route}/${phoneNumber}/${password}")
                }
            )
        }
        composable(ProfileUpdateScreenDestination.route) {
            ProfileVerificationComposable(
                navigateToHomeScreenWithArgs = {
                    navController.navigate("${HomeScreenDestination.route}/${it}")
                },
                navigateToPreviousScreen = {
                    navController.popBackStack()
                }
            )
        }
    }
}