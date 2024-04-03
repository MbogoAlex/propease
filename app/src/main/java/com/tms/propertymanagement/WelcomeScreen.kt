package com.tms.propertymanagement

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tms.propertymanagement.ui.theme.PropEaseTheme

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier
) {
    var items = listOf<Int>(1, 2, 3)
    var currentIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    var painter by rememberSaveable {
        mutableIntStateOf(R.drawable.welcome_illustration)
    }
    var mainText by rememberSaveable {
        mutableStateOf("Welcome to our app")
    }
    var bodyText by rememberSaveable {
        mutableStateOf("Discover, Rent and Manage Properties with Ease")
    }
    when(currentIndex) {
        0 -> {
            painter = R.drawable.welcome_illustration
            mainText = "Welcome to our app"
            bodyText = "Discover, Rent and Manage Properties with Ease"
        }
        1 -> {
            painter = R.drawable.find_property
            mainText = "Find Your Perfect Property"
            bodyText = "Explore a Wide Range of Properties and Find Your Ideal Home"
        }
        2 -> {
            painter = R.drawable.connect_with_owners
            mainText = "Connect with Property Owners"
            bodyText = "Easily Communicate with Property Owners and Schedule Viewings"
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close app",
                    modifier = Modifier
                )
            }
        }
        WelcomeScreenContent(
            painter = painterResource(id = painter),
            mainText = mainText,
            bodyText = bodyText
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CircleDotCurIndex(
                currentIndex = currentIndex,
                items = items
            )
            if(currentIndex > 0) {
                IconButton(
                    onClick = {
                        if(currentIndex != 0) {
                            currentIndex--
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black)
                ) {
                    Icon(
                        tint = Color.White,
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Next"
                    )
                }
            }
            if(currentIndex != 2) {
                IconButton(
                    onClick = {
                        if(currentIndex != 2) {
                            currentIndex++
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black)
                ) {
                    Icon(
                        tint = Color.White,
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next"
                    )
                }
            }
            if(currentIndex == 2) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Signup")
                }
            }

        }
    }
}

@Composable
fun WelcomeScreenContent(
    painter: Painter,
    mainText: String,
    bodyText: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(400.dp)
        )
        Text(
            text = mainText,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = bodyText,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CircleDotCurIndex(
    items: List<Int>,
    currentIndex: Int,
    modifier: Modifier = Modifier
) {
    var color: Color

    Row {
        items.forEachIndexed() { index, item ->

            if(currentIndex == index) {
                color = Color.Blue
            } else {
                color = Color.Gray
            }
            Icon(
                tint = color,
                painter = painterResource(id = R.drawable.circle),
                contentDescription = null,
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CircleDotCurIndexPreview() {
    PropEaseTheme {
        CircleDotCurIndex(
            currentIndex = 0,
            items = listOf(1, 2, 3)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    PropEaseTheme {
        WelcomeScreen()
    }
}