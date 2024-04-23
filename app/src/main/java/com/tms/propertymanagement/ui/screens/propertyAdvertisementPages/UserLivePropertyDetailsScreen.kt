package com.propertymanagement.tms.ui.screens.propertyAdvertisementPages

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.propertymanagement.tms.PropEaseViewModelFactory
import com.propertymanagement.tms.R
import com.propertymanagement.tms.nav.NavigationDestination
import com.propertymanagement.tms.ui.theme.PropEaseTheme
import com.propertymanagement.tms.utils.ReusableFunctions
import com.tms.propertymanagement.connectivity.ConnectivityViewModel

object UserLivePropertyDetailsScreenDestination: NavigationDestination {
    override val title: String = "User Property Details Screen"
    override val route: String = "user-property-details-screen"
    val propertyId: String = "propertyId"
    val routeWithArgs: String = "$route/{$propertyId}"
}
@Composable
fun UserLivePropertyDetailsScreen(
    navigateToPreviousScreen: () -> Unit,
    navigateToPropertyUpdateScreen: (propertyId: String) -> Unit,
    navigateToHomeScreenWithArgs: (childScreen: String) -> Unit,

    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val connectivityViewModel: ConnectivityViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)

    LaunchedEffect(Unit) {
        connectivityViewModel.checkConnectivity(context)
    }

    val viewModel: UserLivePropertyDetailsScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

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

    if(uiState.deletingPropertyStatus == DeletingPropertyStatus.SUCCESS) {
        Toast.makeText(context, "Property removed", Toast.LENGTH_SHORT).show()
        navigateToHomeScreenWithArgs("advertisement-screen")
        viewModel.resetDeletingStatus()
    } else if(uiState.deletingPropertyStatus == DeletingPropertyStatus.FAIL) {
        Toast.makeText(context, "Failed to remove property. Try again later", Toast.LENGTH_SHORT).show()
        viewModel.resetDeletingStatus()
    }

    if(showDeleteDialog) {
        AlertDialog(
            title = {
                Text(text = "Confirm Removal")
            },
            text = {
                Text(text = "Are you sure you want to remove this property? This action cannot be undone.")
            },
            onDismissRequest = {
                showDeleteDialog = !showDeleteDialog
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteProperty()
                    showDeleteDialog = !showDeleteDialog
                }) {
                    Text(text = "Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = !showDeleteDialog
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            if(!uiState.internetPresent) {
                FloatingActionButton(onClick = { viewModel.fetchProperty() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh page"
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigateToPreviousScreen() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous screen"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        enabled = isConnected && uiState.internetPresent,
                        onClick = { navigateToPropertyUpdateScreen(

                            uiState.property.propertyId.toString()
                        ) }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Edit"
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Edit")
                        }
                    }

                }
                if(!isConnected || !uiState.internetPresent) {
                    Text(
                        text = "Check your connection",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                    )
                }
                UserPropertyImageSlider(
                    uiState = uiState
                )
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                )
                UserPropertyListingTextDetails(
                    uiState = uiState,
                    onDeleteButtonClicked = {
                        showDeleteDialog = !showDeleteDialog
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun UserPropertyImageSlider(
    uiState: UserLivePropertyDetailsScreenUiState,
    modifier: Modifier = Modifier
) {
    Log.i("IMAGES_ARE: ", uiState.property.images.toString())
    val pagerState = rememberPagerState(initialPage = 0)
    Column {

        Card {
            Box(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                if(uiState.property.images.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .alpha(0.5f)
                            .height(250.dp)
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
                HorizontalPager(count = uiState.property.images.size, state = pagerState) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(uiState.property.images[page].name)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.loading_img),
                        error = painterResource(id = R.drawable.ic_broken_image),
                        contentScale = ContentScale.Crop,
                        contentDescription = uiState.property.title,
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    )

                }
                if(uiState.isConnected && uiState.property.images.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                            color = Color.White,
                            modifier = Modifier
                                .alpha(0.5f)
                                .background(Color.Black)
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                )
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserPropertyListingTextDetails(
    uiState: UserLivePropertyDetailsScreenUiState,
    onDeleteButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Card {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(Color.Black)
                ) {
                    Text(
                        text = uiState.property.category,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(
                                start = 10.dp,
                                top = 5.dp,
                                end = 10.dp,
                                bottom = 10.dp
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.width(5.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.house),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${uiState.property.rooms} room".takeIf { it.length == 1 } ?: "${uiState.property.rooms} rooms",
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Posted on ${uiState.property.postedDate}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )


        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = uiState.property.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null
            )
            Text(
                text = "${uiState.property.location.county}, ${uiState.property.location.address}",
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        uiState.property.features.distinct().forEachIndexed { index, s ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.circle),
                    contentDescription = null,
                    modifier = Modifier
                        .size(10.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = uiState.property.features[index])
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Divider()
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .heightIn(max = 150.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = uiState.property.description,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Divider()
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ReusableFunctions.formatMoneyValue(uiState.property.price),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "/month"
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Divider()
        Spacer(modifier = Modifier.height(30.dp))
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(uiState.property.user.profilePic == null) {
                Image(
                    painter = painterResource(id = R.drawable.profile_placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(50.dp)
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(uiState.property.user.profilePic)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.ic_broken_image),
                    contentScale = ContentScale.Crop,
                    contentDescription = uiState.property.title,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
            ) {
                Text(
                    text = "${uiState.property.user.fname} ${uiState.property.user.lname}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Owner",
                    fontSize = 13.sp
                )
                Text(
                    text = uiState.property.user.phoneNumber,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Card {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(Color.Green)
                        .padding(10.dp)
                        .clickable {
                            val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${uiState.property.user.phoneNumber}")
                            }
                            context.startActivity(phoneIntent)
                        }
                ) {
                    Icon(
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.phone),
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Card {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(Color.Blue)
                        .padding(10.dp)
                        .clickable {
                            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:${uiState.property.user.phoneNumber}")
                            }
                            context.startActivity(smsIntent)
                        }
                ) {
                    Icon(
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.message),
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            enabled =uiState.isConnected && uiState.internetPresent &&  uiState.deletingPropertyStatus != DeletingPropertyStatus.LOADING,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                onDeleteButtonClicked()
            }
        ) {
            if(uiState.deletingPropertyStatus == DeletingPropertyStatus.LOADING) {
                CircularProgressIndicator()
            } else {
                Text(text = "Remove")
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun UserPropertyImageSliderPreview() {
    PropEaseTheme {
        UserPropertyImageSlider(
            uiState = UserLivePropertyDetailsScreenUiState()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserLivePropertyDetailsPreview() {
    PropEaseTheme {
        UserLivePropertyDetailsScreen(
            navigateToPreviousScreen = {},
            navigateToPropertyUpdateScreen = {},
            navigateToHomeScreenWithArgs = {}
        )
    }
}