package com.tms.propertymanagement

import HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tms.propertymanagement.ui.screens.accountManagement.LoginScreen
import com.tms.propertymanagement.ui.screens.accountManagement.RegistrationScreen
import com.tms.propertymanagement.ui.theme.PropEaseTheme

class MainActivity : ComponentActivity() {
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
                    HomeScreen()
                }
            }
        }
    }
}
