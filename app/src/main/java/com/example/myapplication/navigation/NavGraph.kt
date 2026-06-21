package com.example.myapplication.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.components.MainBottomNavigation
import com.example.myapplication.ui.screens.CameraScreen
import com.example.myapplication.ui.screens.DiscoverScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.ProfileScreen
import com.example.myapplication.ui.screens.SavedScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.FoodViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Discover : Screen("discover")
    object Camera : Screen("camera")
    object Saved : Screen("saved")
    object Profile : Screen("profile")
}

@Composable
fun RecipeNavGraph(navController: NavHostController) {

    val viewModel: FoodViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Discover.route) {
            DiscoverScreen(navController = navController)
        }
        composable("camera") {
            CameraScreen(
                navController = navController,  // Pass navController here
                onCapture = { imagePath ->
                    println("Image captured: $imagePath")
                }
            )
        }
        composable(Screen.Saved.route) {
            SavedScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}

@Composable
fun MainApp(navController: NavHostController) {
    androidx.compose.material3.Scaffold(
        bottomBar = {
            MainBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            RecipeNavGraph(navController = navController)
        }
    }
}