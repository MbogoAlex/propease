package com.propertymanagement.tms.ui.screens.propertyAdvertisementPages

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.R
import com.propertymanagement.tms.apiModel.PropertyData
import com.propertymanagement.tms.ui.theme.PropEaseTheme
import com.tms.propertymanagement.connectivity.ConnectivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserLiveProperties(
    navigateToSpecificUserProperty: (propertyId: String) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToHomeScreenWithArguments: (childScreen: String) -> Unit,
    navigateToLoginScreenWithoutArgs: () -> Unit,
    navigateToLoginScreenWithArgs: (phoneNumber: String, password: String) -> Unit,
    navigateToProfileVerificationScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val connectivityViewModel: ConnectivityViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)

    LaunchedEffect(Unit) {
        connectivityViewModel.checkConnectivity(context)
    }

    BackHandler(onBack = {navigateToHomeScreen()})

    val viewModel: UserLivePropertiesScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

    val isConnected by connectivityViewModel.isConnected.observeAsState(false)
    var active by remember {
        mutableStateOf(false)
    }

    var inactive by remember {
        mutableStateOf(false)
    }

    if(isConnected && !active) {
        Log.i("SETTING_CONNECTIVITY", isConnected.toString())
        viewModel.setConnectionStatus(true)

        active = true
    } else if(!isConnected && active || !isConnected && !inactive) {
        Log.i("FETCH_FROM_LITE", true.toString())
        viewModel.setConnectionStatus(false)
        active = false
        inactive = true
    }

    var showLoginDialog by remember {
        mutableStateOf(false)
    }

    if(uiState.forceLogin) {
        Toast.makeText(context, "Login first to see your adverts", Toast.LENGTH_SHORT).show()
        if(uiState.userDetails.userId != null && uiState.userDetails.userId != 0) {
            Log.i("FORCE_LOGIN_USER", "FORCE LOGIN IN PROGRESS")
            navigateToLoginScreenWithArgs(
                uiState.userDetails.phoneNumber,
                uiState.userDetails.password
            )
        }

        viewModel.resetForcedLogin()
    }

    if(showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = {
                Text(text = "Login")
            },
            text = {
                Text("Only registered users upload and sell properties")
            },
            dismissButton = {
                   TextButton(onClick = { showLoginDialog = !showLoginDialog }) {
                       Text(text = "Dismiss")
                   }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLoginDialog = false
                        navigateToLoginScreenWithoutArgs()
                    },
                ) {
                    Text("Login")
                }
            }
        )
    }

    if(uiState.approvalStatus.lowercase() == "pending" || uiState.approvalStatus.lowercase() == "processing") {
        ProfileVerificationCard(
            approvalStatus = uiState.approvalStatus.lowercase(),
            navigateToProfileVerificationScreen = navigateToProfileVerificationScreen,
            enabled = uiState.approvalStatus.lowercase() != "processing"
        )


    } else if(uiState.approvalStatus.isEmpty() || uiState.approvalStatus.lowercase() == "approved") {
        UserLivePropertiesScreen(
            properties = uiState.properties,
            showPropertyUploadScreen = uiState.showPropertyUploadScreen,
            internetPresent = uiState.internetPresent,
            context = context,
            isConnected = isConnected,
            onFloatingActionButtonClicked = {
                if(!uiState.internetPresent) {
                    Toast.makeText(context, "Connect to the internet", Toast.LENGTH_SHORT).show()
                } else {
                    if(uiState.userDetails.userId != 0 && uiState.userDetails.userId != null) {
                        viewModel.switchToAndFromPropertyUploadScreen()
                    } else {
                        showLoginDialog = !showLoginDialog
                    }
                }
            },
            onRefreshPage = {
                viewModel.fetchUserProperties()
            },
            navigateToHomeScreenWithArguments = navigateToHomeScreenWithArguments,
            navigateToPreviousPage = {
                viewModel.switchToAndFromPropertyUploadScreen()
            },
            navigateToSpecificUserProperty = navigateToSpecificUserProperty
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserLivePropertiesScreen(
    properties: List<PropertyData>,
    showPropertyUploadScreen: Boolean,
    internetPresent: Boolean,
    context: Context,
    isConnected: Boolean,
    onFloatingActionButtonClicked: () -> Unit,
    onRefreshPage: () -> Unit,
    navigateToHomeScreenWithArguments: (childScreen: String) -> Unit,
    navigateToPreviousPage: () -> Unit,
    navigateToSpecificUserProperty: (propertyId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            if(!showPropertyUploadScreen && isConnected && internetPresent) {
                FloatingActionButton(onClick = onFloatingActionButtonClicked) {
                    Icon(
                        painter = painterResource(id = R.drawable.advertise),
                        contentDescription = "Publish a new property"
                    )
                }
            } else if (!internetPresent) {
                FloatingActionButton(onClick = onRefreshPage) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh page"
                    )
                }
            }

        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if(showPropertyUploadScreen) {
                    PropertyUploadScreen(
                        navigateToListingsScreen = { /*TODO*/ },
                        navigateToPreviousPage = navigateToPreviousPage,
                        navigateToHomeScreenWithArguments = {childScreen ->
                            navigateToHomeScreenWithArguments(childScreen)
                        }
                    )
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Your adverts",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (!internetPresent || !isConnected) {
                        Text(
                            text = "Check your internet connection",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    ListingItems(
                        navigateToSpecificUserProperty = navigateToSpecificUserProperty,
                        properties = properties,
                    )
                }

            }
        }
    }
}

@Composable
fun ListingItems(
    navigateToSpecificUserProperty: (propertyId: String) -> Unit,
    properties: List<PropertyData>,
    modifier: Modifier = Modifier
) {
    var properties = properties.reversed()
    if(properties.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(text = "You are yet to upload any property")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            items(properties) {
                ListingItem(
                    navigateToSpecificUserProperty = navigateToSpecificUserProperty,
                    property = it,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
    }

}

@Composable
fun ListingItem(
    property: PropertyData,
//    navigateToUpdateProperty: (propertyId: String) -> Unit,
    navigateToSpecificUserProperty: (propertyId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable {
                navigateToSpecificUserProperty(property.propertyId.toString())
            }
    ) {
        Column {
            if(property.images.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(property.images.first().name)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.ic_broken_image),
                    contentScale = ContentScale.Crop,
                    contentDescription = property.title,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .height(140.dp)
                        .fillMaxWidth()
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .alpha(0.5f)
                        .height(140.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .padding(5.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(5.dp)
                        )
                ) {
                    Text(text = "No image")
                }

            }

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = property.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = property.location.county
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${property.location.county}, ${property.location.address}",
                        fontSize = 13.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Light,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = property.category.takeIf { it.length <= 6 } ?: "${property.category.substring(0, 4)}...",
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(
                                start = 10.dp,
                                top = 5.dp,
                                end = 10.dp,
                                bottom = 5.dp
                            )
                    )
                }


            }
        }
    }
}

@Composable
fun ProfileVerificationCard(
    approvalStatus: String,
    navigateToProfileVerificationScreen: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
   Column(
       modifier = Modifier
           .padding(10.dp)
           .fillMaxSize()
   ) {
       Card(
           shape = RoundedCornerShape(0.dp)
       ) {
           if(approvalStatus == "pending") {
               Text(
                   text = "unverified".uppercase(),
                   fontWeight = FontWeight.Bold,
                   color = Color.Red,
                   modifier = Modifier
                       .padding(5.dp)
               )
           } else if(approvalStatus == "processing") {
               Text(
                   text = "processing".uppercase(),
                   fontWeight = FontWeight.Bold,
                   color = Color.Red,
                   modifier = Modifier
                       .padding(5.dp)
               )
           }

       }

       Spacer(modifier = Modifier.height(20.dp))
       Box(
           modifier = Modifier
               .border(
                   width = 1.dp,
                   color = Color.Gray,
                   shape = RoundedCornerShape(20.dp)
               )
       ) {
           Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier
                   .padding(10.dp)
           ) {
               Icon(
                   tint = Color.Gray,
                   imageVector = Icons.Default.Person,
                   contentDescription = null,
                   modifier = Modifier
                       .padding(10.dp)
                       .size(200.dp)
//                       .background(Color.LightGray)
                       .border(
                           width = 1.dp,
                           color = Color.LightGray,
                           shape = RoundedCornerShape(50.dp)
                       )
               )
               Spacer(modifier = Modifier.weight(1f))
               Column(
                   modifier = Modifier
                       .padding(10.dp)
               ) {
                   Divider(
                       thickness = 5.dp,
                       modifier = Modifier
                   )
                   Spacer(
                       modifier = Modifier
                           .height(40.dp)
                   )
                   Divider(
                       thickness = 5.dp
                   )
                   Spacer(modifier = Modifier.height(40.dp))
                   Divider(
                       thickness = 5.dp
                   )
               }
           }
       }
       Spacer(modifier = Modifier.height(30.dp))
       if(approvalStatus == "pending") {
           Text(
               text = "Verify your identity to start advertising",
               fontSize = 25.sp,
               textAlign = TextAlign.Center,
               modifier = Modifier
                   .align(Alignment.CenterHorizontally)
           )
       } else if(approvalStatus == "processing") {
           Text(
               text = "Your submitted documents are under verification",
               fontSize = 25.sp,
               textAlign = TextAlign.Center,
               modifier = Modifier
                   .align(Alignment.CenterHorizontally)
           )
       }

       Spacer(modifier = Modifier.weight(1f))
       Button(
           enabled = enabled,
           modifier = Modifier
               .fillMaxWidth(),
           onClick = navigateToProfileVerificationScreen
       ) {
           Text(text = "Start verification")
       }
   }
}

@Preview(showBackground = true)
@Composable
fun ProfileVerificationCardScreenPreview() {
    PropEaseTheme {
        ProfileVerificationCard(
            approvalStatus = "pending",
            enabled = false,
            navigateToProfileVerificationScreen = {}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun UserLivePropertiesPreview() {
    PropEaseTheme {
        UserLiveProperties(
            navigateToSpecificUserProperty = {},
            navigateToHomeScreen = {},
            navigateToHomeScreenWithArguments = {},
            navigateToLoginScreenWithoutArgs = {},
            navigateToLoginScreenWithArgs = {phoneNumber, password ->  },
            navigateToProfileVerificationScreen = {}
        )
    }
}
