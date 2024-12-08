

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeauthui.R
import com.example.jetpackcomposeauthui.data.models.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    navController: NavController,
    viewModel: WeatherViewModel = viewModel(),
    onBackClick: () -> Unit
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
                        "FarmTech Weather",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF011101)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                    )
                )
        ) {
            when (val state = weatherState) {
                is WeatherState.Loading -> LoadingScreen()
                is WeatherState.Success -> WeatherContent(state.weather, navController)
                is WeatherState.Error -> ErrorScreen(state.message)
            }
        }
    }
}


@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0xFF011101))
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun WeatherContent(weather: WeatherDto, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WeatherSummaryCard(weather.summary)
        }
        item {
            Button(
                onClick = { navController.navigate("recommendations") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Agricultural Recommendations")
            }
        }
        items(weather.forecasts) { forecast ->
            ForecastItem(forecast)
        }
    }
}

@Composable
fun WeatherSummaryCard(summary: SummaryDto) {
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
                text = "Today's Agricultural Forecast",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF011101)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = summary.phrase,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF011101)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherInfoItem(
                    icon = R.drawable.calendar,
                    title = "Date",
                    value = formatDate(summary.startDate)
                )
                WeatherInfoItem(
                    icon = R.drawable.severity,
                    title = "Severity",
                    value = "${summary.severity}/10",
                    valueColor = when {
                        summary.severity <= 3 -> Color.Green
                        summary.severity <= 6 -> Color(0xFFFFA000)
                        else -> Color.Red
                    }
                )
                WeatherInfoItem(
                    icon = R.drawable.category,
                    title = "category",
                    value = summary.category
                )
            }
        }
    }
}

@Composable
fun WeatherInfoItem(icon: Int, title: String, value: String, valueColor: Color = Color.Black) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun ForecastItem(forecast: ForecastDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(forecast.date),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF011101)
                )
                WeatherIcon(forecast.day.iconCode)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailItem(
                    icon = R.drawable.ic_weather,
                    title = "Temperature",
                    value = "${forecast.temperature.minimum.value}°C - ${forecast.temperature.maximum.value}°C"
                )
                WeatherDetailItem(
                    icon = R.drawable.ic_water,
                    title = "Precipitation",
                    value = "${forecast.day.totalLiquid.value}${forecast.day.totalLiquid.unit}"
                )
                WeatherDetailItem(
                    icon = R.drawable.wind,
                    title = "Wind",
                    value = "${forecast.day.wind.speed.value} ${forecast.day.wind.speed.unit}"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = forecast.day.longPhrase,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF011101)
            )

            Spacer(modifier = Modifier.height(8.dp))
            AirQualitySection(forecast.airAndPollen)
        }
    }
}

@Composable
fun WeatherDetailItem(icon: Int, title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WeatherIcon(iconCode: Int) {
    Image(
        painter = painterResource(id = getWeatherIconResource(iconCode)),
        contentDescription = "Weather Icon",
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0xFFE8F5E9))
            .padding(8.dp)
    )
}

@Composable
fun AirQualitySection(airAndPollen: List<AirAndPollenDto>) {
    Column {
        Text(
            text = "Air Quality & Pollen",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF011101)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            airAndPollen.take(3).forEach { item ->
                AirQualityItem(item)
            }
        }
    }
}

@Composable
fun AirQualityItem(item: AirAndPollenDto) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = item.category,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = when (item.categoryValue) {
                in 0..1 -> Color.Green
                in 2..3 -> Color(0xFFFFA000)
                else -> Color.Red
            }
        )
    }
}

fun formatDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val date = inputFormat.parse(dateString)
    return outputFormat.format(date ?: Date())
}

fun getWeatherIconResource(iconCode: Int): Int {
    return when (iconCode) {
        1, 2, 3, 4 -> R.drawable.sunny
        5, 6, 7, 8 -> R.drawable.partly_cloudy
        11 -> R.drawable.fog
        12, 13, 14, 18 -> R.drawable.rainy
        15, 16, 17 -> R.drawable.thunderstorm
        22, 23, 24, 25, 26, 29 -> R.drawable.snowy
        else -> R.drawable.ic_weather
    }
}

