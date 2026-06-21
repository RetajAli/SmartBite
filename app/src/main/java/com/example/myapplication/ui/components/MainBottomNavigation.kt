package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.R

@Composable
fun MainBottomNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val items = listOf(
        BottomNavItem("home", "Home", R.drawable.ic_home),
        BottomNavItem("discover", "Discover", R.drawable.ic_discover),
        BottomNavItem("camera", "Camera", R.drawable.ic_camera),
        BottomNavItem("saved", "Saved", R.drawable.ic_saved),
        BottomNavItem("profile", "Profile", R.drawable.ic_profile)
    )

    NavigationBar(
        modifier = modifier.height(60.dp),
        containerColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),  // Fixed: using iconResId
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp),
                    )
                },
                label = {
                    Text(
                        text = item.title,
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF5722),  // Orange
                    selectedTextColor = Color(0xFFFF5722),   // Orange
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFFFCCBC)  // Light orange background for selected
                )
            )
        }
    }
}

// Make sure this data class has iconResId property
data class BottomNavItem(
    val route: String,
    val title: String,
    val iconResId: Int  // Changed from 'icon' to 'iconResId'
)