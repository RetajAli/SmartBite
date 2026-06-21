package com.example.myapplication.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.viewmodel.FoodState
import com.example.myapplication.viewmodel.FoodViewModel

@Composable
fun CameraScreen(
    navController: NavController,
    onCapture: (String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: FoodViewModel = viewModel()

    // Permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Camera states
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (!granted) {
                Log.e("CameraScreen", "Camera permission denied")
            }
        }
    )

    // Request permission on start
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Observe food state
    val foodState by viewModel.foodState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Camera preview or captured image
        if (capturedImage != null) {
            // Show captured image with analysis
            CapturedImageWithAnalysis(
                image = capturedImage,
                foodState = foodState,
                onRetake = {
                    capturedImage = null
                    viewModel.resetState()
                },
                onAnalyze = { bitmap ->
                    bitmap?.let { viewModel.analyzeFood(it) }
                }
            )
        } else if (hasCameraPermission) {
            CameraPreview(
                modifier = Modifier.weight(1f),
                onImageCaptureReady = { capture ->
                    imageCapture = capture
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Camera permission required")
            }
        }

        // Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (capturedImage == null) {
                // Capture button
                Button(
                    onClick = {
                        imageCapture?.let { capture ->
                            captureImage(
                                capture = capture,
                                context = context,
                                onSuccess = { bitmap ->
                                    capturedImage = bitmap
                                    onCapture("Image captured")
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Capture Photo")
                }

                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Back")
                }
            }
        }
    }
}

@Composable
fun CapturedImageWithAnalysis(
    image: Bitmap?,
    foodState: FoodState,
    onRetake: () -> Unit,
    onAnalyze: (Bitmap?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Image display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            image?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Captured food image",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Loading indicator
            if (foodState is FoodState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Analysis results
        when (foodState) {
            is FoodState.Success -> {
                val data = foodState.data
                AnalysisResults(data = data, onRetake = onRetake)
            }
            is FoodState.Error -> {
                val error = foodState.message
                ErrorMessage(error = error, onRetake = onRetake, onAnalyze = {
                    image?.let { onAnalyze(it) }
                })
            }
            else -> {
                // Show analyze button if not already analyzing
                if (foodState !is FoodState.Loading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = { onAnalyze(image) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Analyze Food with Spoonacular API")
                        }

                        Button(
                            onClick = onRetake,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text("Retake Photo")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisResults(
    data: com.example.myapplication.data.FoodRecognitionResponse,
    onRetake: () -> Unit
) {
    val recipes = data.recipes?.take(3) ?: emptyList()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
            .padding(16.dp)
    ) {
        // Analysis result card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🎯 Food Analysis Results",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Food Category: ${data.category.name}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Confidence: ${(data.probability * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Recipe suggestions title (if there are recipes)
        if (recipes.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "🍳 Recipe Suggestions",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Recipe items
            items(recipes) { recipe ->
                RecipeCard(recipe = recipe)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Retake button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetake,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Take Another Photo")
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: com.example.myapplication.data.Recipe) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "⏱️ ${recipe.readyInMinutes} mins | 👥 ${recipe.servings} servings",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorMessage(
    error: String,
    onRetake: () -> Unit,
    onAnalyze: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "❌ Analysis Failed",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAnalyze,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Try Again")
        }

        Button(
            onClick = onRetake,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Take Another Photo")
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER

                // Initialize camera
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    // Set up preview use case
                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(surfaceProvider)
                        }

                    // Set up image capture use case
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    onImageCaptureReady(capture)

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            capture
                        )
                    } catch (exc: Exception) {
                        Log.e("CameraPreview", "Camera initialization failed", exc)
                    }
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

private fun captureImage(
    capture: ImageCapture,
    context: Context,
    onSuccess: (Bitmap) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(context)

    capture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes, 0, bytes.size)
                image.close()

                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                onSuccess(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Image capture failed", exception)
            }
        }
    )
}