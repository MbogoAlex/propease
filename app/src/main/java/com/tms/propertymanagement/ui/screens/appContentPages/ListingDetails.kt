package com.tms.propertymanagement.ui.screens.appContentPages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tms.propertymanagement.R
import com.tms.propertymanagement.ui.theme.PropEaseTheme

@Composable
fun ListingDetails(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        ImageSlider()
        Spacer(
            modifier = Modifier
                .height(20.dp)
        )
        ListingTextDetails()
    }
}

@Composable
fun ImageSlider(
    modifier: Modifier = Modifier
) {
    Card {
        Box(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Image(
                painter = painterResource(id = R.drawable.sample_property),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()

            )
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
            ) {
                Text(
                    text = "2/5",
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

@Composable
fun ListingTextDetails(
    modifier: Modifier = Modifier
) {
    var features = listOf<String>("Wi-Fi", "Swimming", "Water", "Security", "Lighting")
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
                        text = "Rental",
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
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Posted on 1-3-2024",
                fontWeight = FontWeight.Light
            )


        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Gracious Apartment in Nairobi",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(features.size) {
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
                    Text(text = features[it])
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
                text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. ",
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Divider()
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "KES 80,000",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "/month"
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Divider()
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
                    .size(120.dp)
            )
            Column(
            ) {
                Text(
                    text = "Evans K",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Owner"
                )
                Text(
                    text = "0794649027"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Card {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(Color.Green)
                        .padding(10.dp)
                ) {
                    Icon(
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.phone),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
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
                ) {
                    Icon(
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.message),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
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
        ImageSlider()
    }
}

@Preview(showBackground = true)
@Composable
fun ListingDetailsPreview() {
    PropEaseTheme {
        ListingDetails()
    }
}