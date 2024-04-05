package com.tms.propertymanagement

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tms.propertymanagement.nav.NavigationGraph

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StartScreen(
    navController: NavHostController = rememberNavController()
) {
    NavigationGraph(navController = navController)
}