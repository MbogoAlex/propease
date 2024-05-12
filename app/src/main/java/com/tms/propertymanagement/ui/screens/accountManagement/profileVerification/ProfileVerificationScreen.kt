package com.tms.propertymanagement.ui.screens.accountManagement.profileVerification

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.R
import com.propertymanagement.tms.nav.NavigationDestination
import com.propertymanagement.tms.ui.theme.PropEaseTheme
import com.propertymanagement.tms.utils.ReusableFunctions

object ProfileVerificationScreenDestination: NavigationDestination {
    override val title: String = "Profile verification screen"
    override val route: String = "profile-verification-screen"

}
@Composable
fun ProfileVerificationComposable(
    navigateToHomeScreenWithArgs: (childScreen: String) -> Unit,
    navigateToPreviousScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val viewModel: ProfileVerificationScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

    if(uiState.executionStatus == ReusableFunctions.ExecutionStatus.SUCCESS) {
        Toast.makeText(context, "Documents uploaded for verification", Toast.LENGTH_SHORT).show()
        navigateToHomeScreenWithArgs("advertisement-screen")
        viewModel.resetUploadingStatus()
    }

    val frontPartUpload = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {uri ->
            if(uri != null) {
                viewModel.uploadFrontPart(uri)
                viewModel.checkIfAllFieldsAreFilled()
            }
        }
    )

    val backPartUpload = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {uri ->
            if(uri != null) {
                viewModel.uploadBackPart(uri)
                viewModel.checkIfAllFieldsAreFilled()
            }
        }
    )

    Box {
        ProfileVerificationScreen(
            onIdFrontUpload = {
                frontPartUpload.launch("image/*")
            },
            onIdBackUpload = {
                backPartUpload.launch("image/*")
            },
            removeFrontPhoto = {
                viewModel.uploadFrontPart(null)
                viewModel.checkIfAllFieldsAreFilled()
            },
            removeBackPhoto = {
                viewModel.uploadBackPart(null)
                viewModel.checkIfAllFieldsAreFilled()
            },
            frontIdUri = uiState.frontImage,
            backIdUri = uiState.backImage,
            saveButtonEnabled = uiState.saveButtonEnabled,
            onUploadDocuments = {
                viewModel.uploadDocuments(context)
            },
            navigateToPreviousScreen = navigateToPreviousScreen,
            executionStatus = uiState.executionStatus
        )
    }
}
@Composable
fun ProfileVerificationScreen(
    onIdFrontUpload: () -> Unit,
    onIdBackUpload: () -> Unit,
    removeFrontPhoto: () -> Unit,
    removeBackPhoto: () -> Unit,
    frontIdUri: Uri?,
    backIdUri: Uri?,
    saveButtonEnabled: Boolean,
    onUploadDocuments: () -> Unit,
    navigateToPreviousScreen: () -> Unit,
    executionStatus: ReusableFunctions.ExecutionStatus,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = navigateToPreviousScreen) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous screen"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "VERIFICATION",
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Upload clear images of the front and back sides of your National ID card",

        )
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .weight(10f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "ID Front:",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            if(frontIdUri != null) {
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(frontIdUri)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.loading_img),
                        error = painterResource(id = R.drawable.ic_broken_image),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Front ID",
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    )
                    IconButton(
                        modifier = Modifier
                            .alpha(0.5f)
                            .background(Color.Black)
                            .padding(
                                start = 5.dp,
                                end = 5.dp,
                            )
                            .align(Alignment.TopEnd),
                        onClick = removeFrontPhoto
                    ) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Remove front id"
                        )
                    }
                }
            } else {
                IdUpload(
                    onIdUpload = onIdFrontUpload,
                    uploadText = "Upload ID front",
                    imageUri = frontIdUri,
                    modifier = Modifier
                        .clickable { onIdFrontUpload() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "ID Back:",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            if(backIdUri != null) {
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(backIdUri)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.loading_img),
                        error = painterResource(id = R.drawable.ic_broken_image),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Front ID",
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    )
                    IconButton(
                        modifier = Modifier
                            .alpha(0.5f)
                            .background(Color.Black)
                            .padding(
                                start = 5.dp,
                                end = 5.dp,
                            )
                            .align(Alignment.TopEnd),
                        onClick = removeBackPhoto
                    ) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Remove back id"
                        )
                    }
                }
            } else {
                IdUpload(
                    onIdUpload = onIdBackUpload,
                    uploadText = "Upload ID back",
                    imageUri = backIdUri,
                    modifier = Modifier
                        .clickable { onIdBackUpload() }
                )
            }


        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            enabled = saveButtonEnabled && executionStatus != ReusableFunctions.ExecutionStatus.LOADING,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onUploadDocuments
        ) {
            if(executionStatus == ReusableFunctions.ExecutionStatus.LOADING) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = "Verify"
                )
            }
        }
    }
}

@Composable
fun IdUpload(
    onIdUpload: () -> Unit,
    uploadText: String,
    imageUri: Uri?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            IconButton(onClick = onIdUpload) {
                Icon(
                    painter = painterResource(id = R.drawable.upload),
                    contentDescription = uploadText
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileVerificationScreenPreview() {
    PropEaseTheme {
        ProfileVerificationScreen(
            onIdFrontUpload = { /*TODO*/ },
            onIdBackUpload = { /*TODO*/ },
            removeFrontPhoto = {},
            removeBackPhoto = {},
            frontIdUri = null,
            backIdUri = null,
            saveButtonEnabled = false,
            onUploadDocuments = {},
            navigateToPreviousScreen = { /*TODO*/ },
            executionStatus = ReusableFunctions.ExecutionStatus.INITIAL
        )
    }
}