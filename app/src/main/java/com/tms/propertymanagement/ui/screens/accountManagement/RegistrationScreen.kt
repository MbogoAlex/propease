package com.tms.propertymanagement.ui.screens.accountManagement

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.tms.propertymanagement.PropEaseViewModelFactory
import com.tms.propertymanagement.R
import com.tms.propertymanagement.nav.NavigationDestination
import com.tms.propertymanagement.ui.theme.PropEaseTheme
import com.tms.propertymanagement.utils.ReusableFunctions

object RegistrationScreenDestination: NavigationDestination {
    override val title: String = "Registration Screen"
    override val route: String = "registration-screen"

}
@Composable
fun RegistrationScreen(
    navigateToPreviousScreen: () -> Unit,
    navigateToLoginScreenWithArguments: (phoneNumber: String, password: String) -> Unit,
    navigateToLoginScreenWithoutArguments: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: RegistrationScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

    if(uiState.registrationStatus == RegistrationStatus.SUCCESS) {
        viewModel.resetRegistrationStatus()
        navigateToLoginScreenWithArguments(
            uiState.registrationDetails.phoneNumber,
            uiState.registrationDetails.password
        )
    } else if(uiState.registrationStatus == RegistrationStatus.FAIL) {
        Toast.makeText(context, uiState.registrationResponseMessage, Toast.LENGTH_SHORT).show()
        viewModel.enableButton()
        viewModel.resetRegistrationStatus()
    }

    var isEmailValid by rememberSaveable {
        mutableStateOf(true)
    }
    fun checkIfEmailIsValid() {
        isEmailValid = ReusableFunctions.checkIfEmailIsValid(uiState.registrationDetails.email)
    }
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
        Text(
            text = "Create your Account",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            RegistrationInputForm(
                leadingIcon = painterResource(id = R.drawable.person),
                labelText = "first name",
                value = uiState.registrationDetails.firstName,
                onValueChanged = {
                                 viewModel.updateFirstName(it)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                isError = false,
                modifier = Modifier
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(20.dp))
            RegistrationInputForm(
                leadingIcon = painterResource(id = R.drawable.person),
                labelText = "last name",
                value = uiState.registrationDetails.lastName,
                isError = false,
                onValueChanged = {
                                 viewModel.updateLastName(it)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier
                    .weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        RegistrationInputForm(
            leadingIcon = painterResource(id = R.drawable.phone),
            labelText = "Phone number",
            value = uiState.registrationDetails.phoneNumber,
            isError = false,
            onValueChanged = {
                             viewModel.updatePhoneNumber(it)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        RegistrationInputForm(
            leadingIcon = painterResource(id = R.drawable.email),
            labelText = "Email",
            value = uiState.registrationDetails.email,
            isError = !isEmailValid,
            onValueChanged = {
                             viewModel.updateEmail(it)
                checkIfEmailIsValid()
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        RegistrationInputForm(
            leadingIcon = painterResource(id = R.drawable.password),
            labelText = "Password",
            value = uiState.registrationDetails.password,
            onValueChanged = {
                             viewModel.updatePassword(it)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            isError = false,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            enabled = uiState.registrationButtonEnabled,
            onClick = {
                checkIfEmailIsValid()
                if(isEmailValid) {
                    viewModel.registerUser()
                } else {
                    Toast.makeText(context, "Enter a valid email", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if(uiState.registrationStatus == RegistrationStatus.LOADING) {
                CircularProgressIndicator()
            }
            else {
                Text(text = "Register")
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(
            onClick = { navigateToLoginScreenWithoutArguments() },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Sign In")
        }

    }
}

@Composable
fun RegistrationInputForm(
    isError: Boolean,
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
        isError = isError,
        keyboardOptions = keyboardOptions,
        onValueChange = onValueChanged,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun InputFormPreview() {
    PropEaseTheme {
        RegistrationInputForm(
            leadingIcon = painterResource(id = R.drawable.person),
            labelText = "Name",
            value = "",
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            isError = false,
            onValueChanged = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    PropEaseTheme {
        RegistrationScreen(
            navigateToPreviousScreen = {},
            navigateToLoginScreenWithArguments = {phoneNumber: String, password: String ->  },
            navigateToLoginScreenWithoutArguments = {}
        )
    }
}