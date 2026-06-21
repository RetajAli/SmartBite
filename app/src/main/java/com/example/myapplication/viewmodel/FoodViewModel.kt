package com.example.myapplication.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.RetrofitClient
import com.example.myapplication.data.FoodRecognitionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FoodViewModel : ViewModel() {

    private val _foodState = MutableStateFlow<FoodState>(FoodState.Idle)
    val foodState: StateFlow<FoodState> = _foodState.asStateFlow()

    fun analyzeFood(imageBitmap: Bitmap) {
        _foodState.value = FoodState.Loading

        viewModelScope.launch {
            try {
                // Save bitmap to file
                val file = saveBitmapToFile(imageBitmap)

                // Create MultipartBody
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                // Make API call
                val response = RetrofitClient.instance.analyzeFoodImage(imagePart)

                _foodState.value = FoodState.Success(response)

                // Clean up temp file
                file.delete()

            } catch (e: Exception) {
                Log.e("FoodViewModel", "Error analyzing food: ${e.message}")
                _foodState.value = FoodState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File {
        // Create temp file
        val file = File.createTempFile("food_image", ".jpg")
        val outputStream = FileOutputStream(file)

        // Compress and save
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()

        return file
    }

    fun resetState() {
        _foodState.value = FoodState.Idle
    }
}

sealed class FoodState {
    object Idle : FoodState()
    object Loading : FoodState()
    data class Success(val data: FoodRecognitionResponse) : FoodState()
    data class Error(val message: String) : FoodState()
}