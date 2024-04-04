package com.tms.propertymanagement

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tms.propertymanagement.nav.NavigationGraph

@Composable
fun StartScreen(
    navController: NavHostController = rememberNavController()
) {
    NavigationGraph(navController = navController)
}