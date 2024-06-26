package com.tms.propertymanagement.ui.screens.accountManagement

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.R
import com.propertymanagement.tms.ui.screens.accountManagement.ProfileScreenViewModel
import com.propertymanagement.tms.ui.screens.accountManagement.UpdatingStatus
import com.propertymanagement.tms.ui.theme.PropEaseTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navigateToHomeScreenWithArgs: (childScreen: String) -> Unit,
    navigateToLoginScreenWithoutArgs: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToUpdateProfileScreen: () -> Unit,
    navigateProfileVerificationScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = {navigateToHomeScreen()})
    val context = LocalContext.current
    val viewModel: ProfileScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var showUpdateProfileScreen by remember {
        mutableStateOf(false)
    }
    var loggingOut by remember {
        mutableStateOf(false)
    }
    if(uiState.updatingStatus == UpdatingStatus.SUCCESS) {
        navigateToHomeScreenWithArgs("profile-screen")
        viewModel.resetProfileUpdateState()
    }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {uri ->
            if(uri != null) {
                viewModel.changeProfilePicture(uri!!, context)
            }

        }
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        if(uiState.uploadedPicture != null) {
            Image(
                painter = rememberImagePainter(data = uiState.uploadedPicture),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(150.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.profile_placeholder),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(150.dp)
            )
        }

        TextButton(onClick = {
//            galleryLauncher.launch("image/*")
        }) {
            Text(
                text = "Change pic"
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Personal Information",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {

                Text(
                    text = "Name",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Email",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Phone No.",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Text(text = uiState.userDetails.userName)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = uiState.userDetails.email)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = uiState.userDetails.phoneNumber)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Icon(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = "Edit name"
                )
                Spacer(modifier = Modifier.height(10.dp))
                Icon(
                    painter = painterResource(id = R.drawable.email),
                    contentDescription = "Edit name"
                )
                Spacer(modifier = Modifier.height(10.dp))
                Icon(
                    painter = painterResource(id = R.drawable.phone),
                    contentDescription = "Edit name"
                )

            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navigateToUpdateProfileScreen()
            }
        ) {
            Text(text = "Update profile")
        }
        if(uiState.userDetails.approvalStatus.lowercase() == "pending") {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navigateProfileVerificationScreen()
                }
            ) {
                Text(text = "Verify identity")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            enabled = !loggingOut,
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                scope.launch {
                    loggingOut = true
                    viewModel.logout()
                    delay(2000)
                    loggingOut = false
                    Toast.makeText(context, "You are logged out", Toast.LENGTH_SHORT).show()
                    navigateToHomeScreenWithArgs("logged-out")
                }

            }
        ) {
            if(loggingOut) {
                CircularProgressIndicator()
            } else {
                Text(text = "Logout")
            }

        }

    }


}

@Composable
fun LoggedOutUserScreen(
    navigateToLoginScreenWithoutArgs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_placeholder),
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(120.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Personal Information",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {

                Text(
                    text = "Name",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Email",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Phone No.",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Text(text = "N/A")
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "N/A")
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "N/A")
            }
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Icon(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = "Edit name"
                )
                Spacer(modifier = Modifier.height(10.dp))
                Icon(
                    painter = painterResource(id = R.drawable.email),
                    contentDescription = "Edit name"
                )
                Spacer(modifier = Modifier.height(10.dp))
                Icon(
                    painter = painterResource(id = R.drawable.phone),
                    contentDescription = "Edit name"
                )

            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { navigateToLoginScreenWithoutArgs() }
        )
        {
            Text(text = "Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoggedOutUserScreenPreview() {
    PropEaseTheme {
        LoggedOutUserScreen(
            navigateToLoginScreenWithoutArgs = {},
        )
    }
}



@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    PropEaseTheme {
        ProfileScreen(
            navigateToHomeScreenWithArgs = {},
            navigateToLoginScreenWithoutArgs = {},
            navigateToHomeScreen = {},
            navigateToUpdateProfileScreen = {},
            navigateProfileVerificationScreen = {}
        )
    }
}