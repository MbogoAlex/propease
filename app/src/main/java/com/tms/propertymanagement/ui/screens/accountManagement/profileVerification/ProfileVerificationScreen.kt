package com.tms.propertymanagement.ui.screens.accountManagement.profileVerification

import android.net.Uri
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.propertymanagement.tms.R
import com.propertymanagement.tms.nav.NavigationDestination
import com.propertymanagement.tms.ui.theme.PropEaseTheme

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
    Box {
        ProfileVerificationScreen(
            onIdFrontUpload = {},
            onIdBackUpload = {},
            frontIdUri = null,
            backIdUri = null,
            navigateToPreviousScreen = navigateToPreviousScreen
        )
    }
}
@Composable
fun ProfileVerificationScreen(
    onIdFrontUpload: () -> Unit,
    onIdBackUpload: () -> Unit,
    frontIdUri: Uri?,
    backIdUri: Uri?,
    navigateToPreviousScreen: () -> Unit,
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
            IdUpload(
                onIdUpload = onIdFrontUpload,
                uploadText = "Upload ID front",
                imageUri = frontIdUri
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "ID Back:",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            IdUpload(
                onIdUpload = onIdBackUpload,
                uploadText = "Upload ID back",
                imageUri = backIdUri
            )

        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { /*TODO*/ }
        ) {
            Text(text = "Verify")
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
    Column {
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
            frontIdUri = null,
            backIdUri = null,
            navigateToPreviousScreen = { /*TODO*/ })
    }
}