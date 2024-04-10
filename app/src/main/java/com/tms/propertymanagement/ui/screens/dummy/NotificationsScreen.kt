package com.propertymanagement.tms.ui.screens.dummy

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.propertymanagement.tms.R

@Composable
fun NotificationsScreen(
    navigateToHomeScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = {navigateToHomeScreen()})
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = "Notifications screen")
        Icon(
            painter = painterResource(id = R.drawable.construction),
            contentDescription = null
        )
        Text(text = "Screen under construction")
    }
}