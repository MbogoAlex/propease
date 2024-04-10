package com.propertymanagement.tms.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.R
import com.propertymanagement.tms.nav.NavigationDestination
import com.propertymanagement.tms.ui.theme.PropEaseTheme
import kotlinx.coroutines.delay

object SplashScreenDestination: NavigationDestination {
    override val title: String = "Splash Screen"
    override val route: String = "splash-screen"

}
@Composable
fun SplashScreen(
    navigateToWelcomeScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SplashScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

    var screenDelay by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        delay(2000L)
        screenDelay = false
    }

    if(!screenDelay) {
        when(uiState.authenticationStatus) {
            AUTHENTICATION_STATUS.INITIAL -> {}
            AUTHENTICATION_STATUS.LOADING -> {}
            AUTHENTICATION_STATUS.SUCCESS -> {
                viewModel.resetAuthenticationStatus()
                if(uiState.loggedInUserData.userId != null) {
                    navigateToHomeScreen()
                } else {
                    navigateToWelcomeScreen()
                }
            }
            AUTHENTICATION_STATUS.FAILURE -> {}
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.prop_ease_3),
            contentDescription = null
        )

        if(screenDelay) {
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PropEaseTheme {
        SplashScreen(
            navigateToWelcomeScreen = {},
            navigateToHomeScreen = {}
        )
    }
}