package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.data.models.NavigationItem
import com.example.myapplication.data.models.Recipe
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.RecipeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val recentRecipes = remember {
        listOf(
            Recipe(
                id = 1,
                title = "Creamy Web Pasta",
                rating = "⭐ 4.5",
                time = "25 min",
                difficulty = "Easy",
                tags = listOf("Vegetarian", "Quick"),
                isVegetarian = true
            ),
            Recipe(
                id = 2,
                title = "Online Chicken Salad",
                rating = "⭐ 4.0",
                time = "15 min",
                difficulty = "Medium",
                tags = listOf("Low Carb"),
                isVegetarian = false
            ),
            Recipe(
                id = 3,
                title = "Spicy Orange Chicken",
                rating = "⭐ 4.8",
                time = "30 min",
                difficulty = "Medium",
                tags = listOf("Asian", "Spicy"),
                isVegetarian = false
            ),
            Recipe(
                id = 4,
                title = "Black Bean Tacos",
                rating = "⭐ 4.2",
                time = "20 min",
                difficulty = "Easy",
                tags = listOf("Vegetarian", "Mexican"),
                isVegetarian = true
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SmartBite",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "AI Recipe Assistant",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "What's cooking today?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Scan ingredients and discover existing recipes.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Scan Ingredients Button
                    // Update the Scan Ingredients button:
                    Button(
                        onClick = {
                            // Navigate to camera screen
                            navController.navigate(Screen.Camera.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_camera),
                                contentDescription = "Scan",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Scan Ingredients",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Recipes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TextButton(
                        onClick = {
                            navController.navigate("discover")
                        }
                    ) {
                        Text(
                            text = "View All",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            items(recentRecipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onRecipeClick = { clickedRecipe ->
                        Toast.makeText(context, "Opening ${clickedRecipe.title}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}