package com.propertymanagement.tms.ui.screens.propertyAdvertisementPages

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.propertymanagement.tms.R
import com.propertymanagement.tms.utils.ReusableFunctions
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PropertyUploadPreviewScreen(
    viewModel: PropertyUploadScreenViewModel,
    uiState: PropertyUploadScreenUiState,
    navigateToPreviousScreen: () -> Unit,
    navigateToHomeScreenWithArguments: (childScreen: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var context = LocalContext.current

    var showDialog by remember {
        mutableStateOf(false)
    }

    BackHandler(
        onBack = navigateToPreviousScreen
    )

    if(showDialog) {
        AlertDialog(
            title = {
                Text(text = "Uploading error")
            },
            text = {
                Text(text = uiState.uploadingPropertyResponse)
            },
            onDismissRequest = {
                showDialog = !showDialog
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = !showDialog

                    }
                ) {
                    Text(text = "Exit")
                }
            },

            )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextButton(onClick = { navigateToPreviousScreen() }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous screen"
                    )
                    Text(
                        text = "Continue editing"
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(0.dp),
                onClick = {
                    viewModel.uploadProperty(context)
                },
                enabled = uiState.saveButtonEnabled,
                modifier = Modifier
                    .width(130.dp)
            ) {
                if(uiState.uploadingStatus == UploadingStatus.LOADING) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                    )
                } else {
                    Text(text = "Publish")
                    Icon(
                        painter = painterResource(id = R.drawable.advertise),
                        contentDescription = "Upload property"
                    )
                }


            }
        }
        ImageSlider(
            uiState = uiState
        )
        Spacer(
            modifier = Modifier
                .height(20.dp)
        )
        ListingTextDetails(
            uiState = uiState
        )
    }

    if(uiState.uploadingStatus == UploadingStatus.SUCCESS) {
        Toast.makeText(context, "Your property is live", Toast.LENGTH_SHORT).show()
        navigateToHomeScreenWithArguments("advertisement-screen")
        viewModel.resetSavingState()
    } else if(uiState.uploadingStatus == UploadingStatus.FAILURE)  {
        showDialog = !showDialog
        viewModel.resetSavingState()
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider(
    uiState: PropertyUploadScreenUiState,
    modifier: Modifier = Modifier
) {
    Log.i("IMAGES_ARE: ", uiState.images.toString())
    val pagerState = rememberPagerState(initialPage = 0)
    Column {

        Card {
            Box(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                HorizontalPager(count = uiState.images.size, state = pagerState) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(uiState.images[page])
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.loading_img),
                        error = painterResource(id = R.drawable.ic_broken_image),
                        contentScale = ContentScale.Crop,
                        contentDescription = uiState.title,
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    )

                }
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListingTextDetails(
    uiState: PropertyUploadScreenUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column {
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
                        text = uiState.category.name,
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
                    text = "${uiState.numberOfRooms} room".takeIf { uiState.numberOfRooms == 1 }
                        ?: "${uiState.numberOfRooms} rooms",
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Posted on ${LocalDate.now()}",
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            )


        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = uiState.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(uiState.features.size) {
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
                    Text(text = uiState.features[it])
                }

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
                text = uiState.description,
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
                text = ReusableFunctions.formatMoneyValue(uiState.price.toDouble()),
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
            Image(
                painter = painterResource(id = R.drawable.profile_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
            ) {
                Text(
                    text = uiState.userDetails.userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Owner",
                    fontSize = 13.sp
                )
                Text(
                    text = uiState.userDetails.phoneNumber,
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
                                data = Uri.parse("tel:${uiState.userDetails.phoneNumber}")
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
                                data = Uri.parse("smsto:${uiState.userDetails.phoneNumber}")
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
    }

}