package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class FoodRecognitionResponse(
    @SerializedName("category")
    val category: FoodCategory,

    @SerializedName("probability")
    val probability: Double,

    @SerializedName("recipes")
    val recipes: List<Recipe>? = emptyList()
)

data class FoodCategory(
    @SerializedName("name")
    val name: String,

    @SerializedName("probability")
    val probability: Double
)

data class Recipe(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("readyInMinutes")
    val readyInMinutes: Int,

    @SerializedName("servings")
    val servings: Int
)