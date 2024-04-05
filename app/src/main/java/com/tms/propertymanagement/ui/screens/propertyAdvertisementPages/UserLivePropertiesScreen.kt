package com.tms.propertymanagement.ui.screens.propertyAdvertisementPages

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.tms.propertymanagement.PropEaseViewModelFactory
import com.tms.propertymanagement.R
import com.tms.propertymanagement.apiModel.PropertyData
import com.tms.propertymanagement.ui.theme.PropEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserLiveProperties(
    navigateToSpecificUserProperty: (propertyId: String) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToHomeScreenWithArguments: (childScreen: String) -> Unit,
    navigateToLoginScreenWithoutArgs: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = {navigateToHomeScreen()})
    val viewModel: UserLivePropertiesScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

    var showLoginDialog by remember {
        mutableStateOf(false)
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

    Scaffold(
        floatingActionButton = {
            if(!uiState.showPropertyUploadScreen) {
                FloatingActionButton(
                    onClick = {
                        if(uiState.userDetails.userId != 0) {
                            viewModel.switchToAndFromPropertyUploadScreen()
                        } else {
                            showLoginDialog = !showLoginDialog
                        }
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.advertise),
                        contentDescription = "Publish a new property"
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
                if(uiState.showPropertyUploadScreen) {
                    PropertyUploadScreen(
                        navigateToListingsScreen = { /*TODO*/ },
                        navigateToPreviousPage = {
                            viewModel.switchToAndFromPropertyUploadScreen()
                        },
                        navigateToHomeScreenWithArguments = {childScreen ->
                            navigateToHomeScreenWithArguments(childScreen)
                        }
                    )
                } else {
                    ListingItems(
                        navigateToSpecificUserProperty = navigateToSpecificUserProperty,
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }

            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

    }
}

@Composable
fun ListingItems(
    navigateToSpecificUserProperty: (propertyId: String) -> Unit,
    uiState: UserLivePropertiesScreenUiState,
    viewModel: UserLivePropertiesScreenViewModel,
    modifier: Modifier = Modifier
) {
    var properties = uiState.properties.reversed()
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
                Image(
                    painter = painterResource(id = R.drawable.no_image_icon_coming_soon),
                    contentDescription = "No image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .alpha(0.5f)
                        .height(140.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = property.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = property.location.county
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = property.location.county.takeIf { property.location.county.length <= 6 } ?: "${property.location.county.substring(0, 4)}...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFa2a832)
                        )
                    ) {
                        Text(
                            text = property.category.takeIf { it.length <= 6 } ?: "${property.category.substring(0, 4)}...",
                            fontSize = 14.sp,
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
            navigateToLoginScreenWithoutArgs = {}
        )
    }
}
