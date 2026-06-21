package com.example.myapplication.data.models

data class Recipe(
    val id: Int,
    val title: String,
    val rating: String,
    val time: String,
    val difficulty: String,
    val tags: List<String>,
    val isVegetarian: Boolean
)