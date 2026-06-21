package com.example.myapplication.api

import com.example.myapplication.data.FoodRecognitionResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface SpoonacularService {
    @Multipart
    @POST("food/images/analyze")
    suspend fun analyzeFoodImage(
        @Part image: MultipartBody.Part,
        @Query("apiKey") apiKey: String = "66f1b5ef28a64f83b81fbb0db7b72ae2"
    ): FoodRecognitionResponse
}