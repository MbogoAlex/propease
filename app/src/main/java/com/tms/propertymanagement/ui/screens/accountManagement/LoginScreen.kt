package com.propertymanagement.tms.ui.screens.accountManagement

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.R
import com.propertymanagement.tms.nav.NavigationDestination
import com.propertymanagement.tms.ui.theme.PropEaseTheme
import kotlin.math.log


object LoginScreenDestination: NavigationDestination {
    override val title: String = "Login Screen"
    override val route: String = "login-screen"
    val phoneNumber: String = "phoneNumber"
    val password: String = "password"
    val routeArgs: String = "$route/{$phoneNumber}/{$password}"

}
@Composable
fun LoginScreen(
    navigateToPreviousScreen: () -> Unit,
    navigateToRegistrationScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: LoginScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

    if(uiState.loginStatus == LoginStatus.SUCCESS) {
        viewModel.resetLoginStatus()
        navigateToHomeScreen()
    } else if(uiState.loginStatus == LoginStatus.FAILURE) {
        Toast.makeText(context, uiState.loginResponseMessage, Toast.LENGTH_SHORT).show()
        viewModel.enableButton()
        viewModel.resetLoginStatus()
    }

    Box(
        modifier = modifier
    ) {
        LoginScreenBody(
            navigateToPreviousScreen = { navigateToPreviousScreen() },
            phoneNumber = uiState.loginDetails.phoneNumber,
            onUpdatePhoneNumber = {
                viewModel.updatePhoneNumber(it)
            },
            password = uiState.loginDetails.password,
            onUpdatePassword = {
                viewModel.updatePassword(it)
            },
            loginButtonEnabled = uiState.loginButtonEnabled,
            loginUser = {
                viewModel.loginUser()
            },
            loginStatus = uiState.loginStatus,
            navigateToRegistrationScreen = {
                navigateToRegistrationScreen()
            }
        )
    }

}

@Composable
fun LoginScreenBody(
    navigateToPreviousScreen: () -> Unit,
    phoneNumber: String,
    onUpdatePhoneNumber: (phoneNumber: String) -> Unit,
    password: String,
    onUpdatePassword: (password: String) -> Unit,
    loginButtonEnabled: Boolean,
    loginUser: () -> Unit,
    loginStatus: LoginStatus,
    navigateToRegistrationScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row {
            IconButton(onClick = { navigateToPreviousScreen() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous page"
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.prop_ease_3),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Welcome back",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        LoginInputForm(
            leadingIcon = painterResource(id = R.drawable.phone),
            labelText = "Phone number",
            value = phoneNumber,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Phone
            ),
            onValueChanged = onUpdatePhoneNumber,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        LoginInputForm(
            leadingIcon = painterResource(id = R.drawable.password),
            labelText = "Password",
            value = password,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            onValueChanged = onUpdatePassword,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            enabled = loginButtonEnabled,
            onClick = loginUser,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if(loginStatus == LoginStatus.LOADING) {
                CircularProgressIndicator()
            }
            else {
                Text(text = "Sign in")
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(
            onClick = { navigateToRegistrationScreen() },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Register")
        }
    }
}

@Composable
fun LoginInputForm(
    leadingIcon: Painter,
    labelText: String,
    value: String,
    keyboardOptions: KeyboardOptions,
    onValueChanged: (newValue: String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        shape = RoundedCornerShape(10.dp),
        leadingIcon = {
            Icon(
                painter = leadingIcon,
                contentDescription = null
            )
        },

        label = {
            Text(text = labelText)
        },
        value = value,
        keyboardOptions = keyboardOptions,
        onValueChange = onValueChanged,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PropEaseTheme {
        LoginScreenBody(
            navigateToPreviousScreen = { /*TODO*/ },
            phoneNumber = "",
            onUpdatePhoneNumber = {},
            password = "",
            onUpdatePassword = {},
            loginButtonEnabled = false,
            loginUser = { /*TODO*/ },
            loginStatus = LoginStatus.INITIAL,
            navigateToRegistrationScreen = { /*TODO*/ })
    }
}