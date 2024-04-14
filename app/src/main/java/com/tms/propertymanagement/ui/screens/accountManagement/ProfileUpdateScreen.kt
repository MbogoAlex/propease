package com.tms.propertymanagement.ui.screens.accountManagement

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.nav.NavigationDestination
import com.propertymanagement.tms.ui.screens.accountManagement.ProfileScreenUiState
import com.propertymanagement.tms.ui.screens.accountManagement.ProfileScreenViewModel

object ProfileUpdateScreenDestination: NavigationDestination {
    override val title: String = "Profile update screen"
    override val route: String = "profile-update-screen"

}
@Composable
fun ProfileUpdateScreen(
    navigateToHomeScreenWithArgs: (childScreen: String) -> Unit,
    navigateToLoginScreenWithArgs: (phoneNumber: String, password: String) -> Unit,
    navigateToPreviousScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: ProfileUpdateScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()
    if(uiState.updatingUserProfileStatus == UpdatingUserProfileStatus.SUCCESS) {
        viewModel.resetUpdatingStatus()
        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        navigateToHomeScreenWithArgs("profile-screen")

    } else if(uiState.updatingUserProfileStatus == UpdatingUserProfileStatus.FAIL) {
        viewModel.resetUpdatingStatus()
        Toast.makeText(context, "Failed to update profile. Try gain later", Toast.LENGTH_SHORT).show()
    }
    if(uiState.forcedLogin) {
        Toast.makeText(context, "Login first to continue with this operation", Toast.LENGTH_SHORT).show()
        navigateToLoginScreenWithArgs(
            uiState.userDetails.phoneNumber,
            uiState.userDetails.password
        )
        viewModel.resetForcedLogin()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row {
            IconButton(onClick = { navigateToPreviousScreen() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous screen"
                )
            }
        }
        Text(
            text = "New profile details",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        ProfileUpdateInputForm(
            labelText = "First Name",
            value = uiState.firstName,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            onValueChanged = {
                             viewModel.updateFirstName(it)
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        ProfileUpdateInputForm(
            labelText = "Last Name",
            value = uiState.lastName,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            onValueChanged = {
                             viewModel.updateLastName(it)
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        ProfileUpdateInputForm(
            labelText = "Email",
            value = uiState.email,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Email
            ),
            onValueChanged = {
                             viewModel.updateEmail(it)
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        ProfileUpdateInputForm(
            labelText = "Phone number",
            value = uiState.phoneNumber,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            onValueChanged = {
                             viewModel.updatePhoneNumber(it)
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            enabled = (uiState.enableUpdateButton && uiState.updatingUserProfileStatus != UpdatingUserProfileStatus.LOADING),
            onClick = {
                      viewModel.updateUserProfile()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if(uiState.updatingUserProfileStatus == UpdatingUserProfileStatus.LOADING) {
                CircularProgressIndicator()
            } else {
                Text(text = "Update")
            }

        }
    }
}

@Composable
fun ProfileUpdateInputForm(
    labelText: String,
    value: String,
    maxLines: Int,
    keyboardOptions: KeyboardOptions,
    onValueChanged: (newValue: String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        shape = RoundedCornerShape(10.dp),

        label = {
            Text(text = labelText)
        },
        maxLines = maxLines,
        value = value,
        keyboardOptions = keyboardOptions,
        onValueChange = onValueChanged,
        modifier = modifier
    )
}

//@Preview(showBackground = true)
//@Composable
//fun ProfileUpdateInputFormPreview() {
//
//    PropEaseTheme {
//        ProfileUpdateScreen(
//            uiState = ProfileScreenUiState(),
//            viewModel = ProfileScreenViewModel("", "")
//        )
//    }
//}