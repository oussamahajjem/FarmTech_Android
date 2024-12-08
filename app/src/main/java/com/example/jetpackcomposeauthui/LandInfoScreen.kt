package com.example.jetpackcomposeauthui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.jetpackcomposeauthui.data.models.LandInfo
import com.example.jetpackcomposeauthui.components.MapSelector
import com.example.jetpackcomposeauthui.data.models.LandInfoState
import com.example.jetpackcomposeauthui.data.models.LandInfoViewModel
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandInfoScreen(
    navController: NavHostController,
    viewModel: LandInfoViewModel = viewModel()
) {
    val context = LocalContext.current
    var area by remember { mutableStateOf("") }
    var soilType by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf(GeoPoint(0.0, 0.0)) }

    val landInfoState by viewModel.landInfoState.collectAsState()
    val landInfoList by viewModel.landInfoList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getLandInfoList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Land Information",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = area,
            onValueChange = { area = it },
            label = { Text("Area (sq m)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = soilType,
            onValueChange = { soilType = it },
            label = { Text("Soil Type") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Select Location on Map",
            style = MaterialTheme.typography.bodyLarge
        )

        MapSelector(
            context = context,
            onLocationSelected = { geoPoint ->
                selectedLocation = geoPoint
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Text(
            text = "Selected Location: Lat: ${selectedLocation.latitude}, Lng: ${selectedLocation.longitude}",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = {
                viewModel.createLandInfo(
                    area = area.toDoubleOrNull() ?: 0.0,
                    soilType = soilType,
                    latitude = selectedLocation.latitude,
                    longitude = selectedLocation.longitude
                )
                // Clear input fields after submission
                area = ""
                soilType = ""
                selectedLocation = GeoPoint(0.0, 0.0)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit")
        }

        when (val state = landInfoState) {
            is LandInfoState.Loading -> CircularProgressIndicator()
            is LandInfoState.Success -> {
                LazyColumn {
                    items(landInfoList) { landInfo ->
                        LandInfoItem(landInfo)
                    }
                }
            }
            is LandInfoState.Error -> Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            else -> {}
        }
    }
}

@Composable
fun LandInfoItem(landInfo: LandInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Area: ${landInfo.area} sq m", style = MaterialTheme.typography.bodyLarge)
            Text("Soil Type: ${landInfo.soilType}", style = MaterialTheme.typography.bodyMedium)
            Text("Location: (${landInfo.latitude}, ${landInfo.longitude})", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

