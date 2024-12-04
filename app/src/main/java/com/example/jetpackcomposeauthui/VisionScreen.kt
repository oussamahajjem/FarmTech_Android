import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.jetpackcomposeauthui.data.models.VisionResponse
import com.example.jetpackcomposeauthui.data.models.VisionViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisionScreen(
    navController: NavHostController,
    viewModel: VisionViewModel = viewModel()
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf("") }
    val visionResult by viewModel.visionResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                imageUri?.let { uri ->
                    viewModel.analyzeImage(context, uri)
                }
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
                viewModel.analyzeImage(context, it)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crop Health Analysis") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ImageInputSection(
                    imageUri = imageUri,
                    onCaptureClick = {
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        val imageFile = File(context.filesDir, "JPEG_${timeStamp}.jpg")
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            imageFile
                        )
                        imageUri = uri
                        cameraLauncher.launch(uri)
                    },
                    onGalleryClick = {
                        galleryLauncher.launch("image/*")
                    }
                )
            }

            item {
                UrlInputSection(
                    imageUrl = imageUrl,
                    onUrlChange = { imageUrl = it },
                    onAnalyzeClick = {
                        viewModel.analyzeImageFromUrl(imageUrl)
                    }
                )
            }

            item {
                when {
                    isLoading -> LoadingIndicator()
                    error != null -> ErrorDisplay(error!!)
                    visionResult != null -> VisionResultDisplay(visionResult!!)
                }
            }
        }
    }
}

@Composable
fun ImageInputSection(
    imageUri: Uri?,
    onCaptureClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCaptureClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Capture")
                }

                Button(
                    onClick = onGalleryClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Upload")
                }
            }

            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Crop Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlInputSection(
    imageUrl: String,
    onUrlChange: (String) -> Unit,
    onAnalyzeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = imageUrl,
                onValueChange = onUrlChange,
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onAnalyzeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Analyze URL")
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorDisplay(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun VisionResultDisplay(result: VisionResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Analysis Results", style = MaterialTheme.typography.headlineSmall)

            CropHealthSection(result)
            DiseaseAndPestSection(result)
            CategorySection(result)
        }
    }
}

@Composable
fun CropHealthSection(result: VisionResponse) {
    val cropHealthScore = result.categories.find { it.name.toLowerCase().contains("plant") }?.score ?: 0.0
    val healthStatus = when {
        cropHealthScore >= 0.8 -> "Excellent"
        cropHealthScore >= 0.6 -> "Good"
        cropHealthScore >= 0.4 -> "Fair"
        else -> "Poor"
    }

    Column {
        Text("Crop Health", style = MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(
            progress = cropHealthScore.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Text(
            "Status: $healthStatus",
            style = MaterialTheme.typography.bodyMedium,
            color = when (healthStatus) {
                "Excellent" -> androidx.compose.ui.graphics.Color.Green
                "Good" -> androidx.compose.ui.graphics.Color(0xFF8BC34A)
                "Fair" -> androidx.compose.ui.graphics.Color(0xFFFFC107)
                else -> androidx.compose.ui.graphics.Color.Red
            }
        )
    }
}

@Composable
fun DiseaseAndPestSection(result: VisionResponse) {
    Column {
        Text("Disease and Pest Detection", style = MaterialTheme.typography.titleMedium)
        Text(
            "Disease Detected: ${if (result.diseaseDetected) "Yes" else "No"}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (result.diseaseDetected) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.Green
        )
        Text(
            "Pests Detected: ${if (result.pestsDetected) "Yes" else "No"}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (result.pestsDetected) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.Green
        )
    }
}

@Composable
fun CategorySection(result: VisionResponse) {
    Column {
        Text("Categories", style = MaterialTheme.typography.titleMedium)
        result.categories.take(5).forEach { category ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(category.name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    String.format("%.2f", category.score),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}

