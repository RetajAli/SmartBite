package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.models.NavigationItem

@Composable
fun RecipeAssistantBottomNavigation(
    navigationItems: List<NavigationItem>,
    currentRoute: String,
    onNavigationItemClick: (NavigationItem) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(60.dp)
    ) {
        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = item.title == currentRoute,
                onClick = { onNavigationItemClick(item) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) }
            )
        }
    }
}