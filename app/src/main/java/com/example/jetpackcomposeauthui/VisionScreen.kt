package com.example.jetpackcomposeauthui.ui.screens

import VisionResponse
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposeauthui.data.models.VisionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisionScreen(
    navController: NavHostController,
    viewModel: VisionViewModel = viewModel()
) {
    var imageUrl by remember { mutableStateOf("") }
    val visionResult by viewModel.visionResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.analyzeImage(imageUrl) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Analyze Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            visionResult != null -> VisionResultDisplay(visionResult!!)
        }
    }
}

@Composable
fun VisionResultDisplay(result: VisionResponse) {
    LazyColumn {
        item {
            Text("Categories:", style = MaterialTheme.typography.headlineSmall)
            result.categories.forEach { category ->
                Text("${category.name}: ${category.score}")
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Description:", style = MaterialTheme.typography.headlineSmall)
            Text("Tags: ${result.description.tags.joinToString(", ")}")
            result.description.captions.forEach { caption ->
                Text("Caption: ${caption.text} (Confidence: ${caption.confidence})")
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Color Information:", style = MaterialTheme.typography.headlineSmall)
            Text("Dominant Foreground Color: ${result.color.dominantColorForeground}")
            Text("Dominant Background Color: ${result.color.dominantColorBackground}")
            Text("Dominant Colors: ${result.color.dominantColors.joinToString(", ")}")
            Text("Accent Color: ${result.color.accentColor}")
            Text("Is Black and White: ${result.color.isBwImg}")
        }
    }
}