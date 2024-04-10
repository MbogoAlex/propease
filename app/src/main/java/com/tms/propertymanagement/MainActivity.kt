package com.propertymanagement.tms

import HomeScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.propertymanagement.tms.ui.screens.accountManagement.LoginScreen
import com.propertymanagement.tms.ui.screens.accountManagement.RegistrationScreen
import com.propertymanagement.tms.ui.screens.propertyAdvertisementPages.PropertyUploadScreen
import com.propertymanagement.tms.ui.theme.PropEaseTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PropEaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    WelcomeScreen()
//                    RegistrationScreen()
//                    LoginScreen()
//                    HomeScreen()
                    StartScreen()
//                    PropertyUploadScreen()
                }
            }
        }
    }
}
