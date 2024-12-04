package com.example.jetpackcomposeauthui

import ErrorScreen
import LoadingScreen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeauthui.data.models.WeatherDto
import com.example.jetpackcomposeauthui.data.models.WeatherState
import com.example.jetpackcomposeauthui.data.models.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    navController: NavController,
    viewModel: WeatherViewModel = viewModel()
) {
    val weatherState by viewModel.weatherState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.fetchWeather()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agricultural Recommendations",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF011101)
                )
            )
        }
    ) { innerPadding ->
        when (val state = weatherState) {
            is WeatherState.Loading -> LoadingScreen()
            is WeatherState.Success -> RecommendationsContent(state.weather, Modifier.padding(innerPadding))
            is WeatherState.Error -> ErrorScreen(state.message)
        }
    }
}

@Composable
fun RecommendationsContent(weather: WeatherDto, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            RecommendationCard("Irrigation", getIrrigationRecommendation(weather))
        }
        item {
            RecommendationCard("Planting", getPlantingRecommendation(weather))
        }
        item {
            RecommendationCard("Plant Disease Risk", getPlantDiseaseRisk(weather))
        }
        item {
            RecommendationCard("Fruit Tree Care", getFruitTreeRecommendation(weather))
        }
        item {
            RecommendationCard("Crop Selection", getCropSelectionRecommendation(weather))
        }
    }
}

@Composable
fun RecommendationCard(title: String, recommendation: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF011101)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recommendation,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}

