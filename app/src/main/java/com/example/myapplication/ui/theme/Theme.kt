package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF5722), // Orange
    secondary = Color(0xFFFF9800), // Light Orange
    tertiary = Color(0xFFFFCC80), // Light Orange/Beige

    background = Color.White,
    surface = Color.White,

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,

    primaryContainer = Color(0xFFFFCCBC),
    onPrimaryContainer = Color(0xFFBF360C),
    error = Color(0xFFD32F2F),
)

@Composable
fun RecipeAssistantTheme(
    darkTheme: Boolean = false, // Force light mode by setting default to false
    content: @Composable () -> Unit
) {
    // Always use light color scheme
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}