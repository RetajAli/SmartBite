package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.MainApp
import com.example.myapplication.ui.theme.RecipeAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeAssistantTheme {
                val navController = rememberNavController()
                MainApp(navController = navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    RecipeAssistantTheme {
        val navController = rememberNavController()
        MainApp(navController = navController)
    }
}