package com.tms.propertymanagement

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tms.propertymanagement.ui.theme.PropEaseTheme

@Composable
fun SplashScreen(
    navigateToWelcomeScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SplashScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

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
        Text(
            text = "PropEase",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        if(uiState.authenticationStatus == AUTHENTICATION_STATUS.LOADING) {
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