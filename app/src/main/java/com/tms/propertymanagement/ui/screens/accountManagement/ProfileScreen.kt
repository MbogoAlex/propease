package com.tms.propertymanagement.ui.screens.accountManagement

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tms.propertymanagement.R
import com.tms.propertymanagement.ui.theme.PropEaseTheme

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_placeholder),
            contentDescription = "Profile picture"
        )
        Text(
            text = "Change your profile pic"
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Personal Information")
        Spacer(modifier = Modifier.height(20.dp))

    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    PropEaseTheme {
        ProfileScreen()
    }
}