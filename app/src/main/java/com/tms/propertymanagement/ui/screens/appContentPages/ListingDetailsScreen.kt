package com.propertymanagement.tms.ui.screens.appContentPages

import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

object ListingDetailsDestination: NavigationDestination {
    override val title: String = "Listing Details Screen"
    override val route: String = "listing-details-screen"
    val propertyId: String = "propertyId"
    val routeWithArgs: String = "$route/{$propertyId}"
}
@Composable
fun ListingDetailsScreen(
    navigateToPreviousScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val connectivityViewModel: ConnectivityViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)

    LaunchedEffect(Unit) {
        connectivityViewModel.checkConnectivity(context)
    }

    val isConnected by connectivityViewModel.isConnected.observeAsState(false)

    val viewModel: ListingDetailsScreenViewModel = viewModel(factory = PropEaseViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsState()

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
        Box(
            modifier = Modifier
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()

            ) {
                IconButton(
                    onClick = { navigateToPreviousScreen() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous screen"
                    )
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
                ImageSlider(
                    uiState = uiState,
                )
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                )
                ListingTextDetails(
                    uiState = uiState,
                )


            }
        }
    }


}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider(
    uiState: ListingDetailsScreenUiState,
    modifier: Modifier = Modifier
) {
    Log.i("IMAGES_ARE: ", uiState.property.images.toString())
    val pagerState = rememberPagerState(initialPage = 0)
    Column(modifier = modifier) {

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
fun ListingTextDetails(
    uiState: ListingDetailsScreenUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
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
                        fontSize = 14.sp,
                        color = Color.White,
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
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            )


        }
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
        Text(
            text = uiState.property.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
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
        BottomSheet(
            uiState = uiState,
        )

    }
}

@Composable
fun BottomSheet(
    uiState: ListingDetailsScreenUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column {
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
            Spacer(modifier = Modifier.width(20.dp))
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
                            .size(20.dp)
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
                            .size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageSliderPreview() {
    PropEaseTheme {
        ImageSlider(
            uiState = ListingDetailsScreenUiState()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListingDetailsPreview() {
    PropEaseTheme {
        ListingDetailsScreen(
            navigateToPreviousScreen = {}
        )
    }
}