import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeauthui.data.models.WeatherDto
import kotlin.math.roundToInt

@Composable
fun WeatherRecommendations(weather: WeatherDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
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
                text = "Agricultural Recommendations",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
            Spacer(modifier = Modifier.height(8.dp))

            RecommendationItem(
                title = "Irrigation",
                recommendation = getIrrigationRecommendation(weather)
            )

            RecommendationItem(
                title = "Planting",
                recommendation = getPlantingRecommendation(weather)
            )

            RecommendationItem(
                title = "Plant Disease Risk",
                recommendation = getPlantDiseaseRisk(weather)
            )

            RecommendationItem(
                title = "Fruit Tree Care",
                recommendation = getFruitTreeRecommendation(weather)
            )

            RecommendationItem(
                title = "Crop Selection",
                recommendation = getCropSelectionRecommendation(weather)
            )
        }
    }
}

@Composable
fun RecommendationItem(title: String, recommendation: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF33691E)
        )
        Text(
            text = recommendation,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

fun getIrrigationRecommendation(weather: WeatherDto): String {
    val forecast = weather.forecasts.firstOrNull() ?: return "No forecast data available."
    val precipitation = forecast.day.totalLiquid.value
    val temperature = forecast.temperature.maximum.value

    return when {
        precipitation > 15.0 -> "Natural rainfall is sufficient. No irrigation needed. Consider drainage management for excess water."
        precipitation > 10.0 -> "Light irrigation may be needed for water-intensive crops like tomatoes and cucumbers."
        precipitation > 5.0 -> "Moderate irrigation recommended. Focus on deep-rooted crops like olives and grapes."
        temperature > 35.0 -> "High temperatures expected. Increase irrigation frequency for all crops, especially for citrus trees and vegetables."
        temperature > 30.0 -> "Warm conditions. Ensure regular irrigation for fruit trees and heat-sensitive crops."
        else -> "Regular irrigation recommended. Monitor soil moisture levels, particularly for young plantings and shallow-rooted crops."
    }
}

fun getPlantingRecommendation(weather: WeatherDto): String {
    val forecast = weather.forecasts.firstOrNull() ?: return "No forecast data available."
    val temperature = forecast.temperature.maximum.value
    val precipitation = forecast.day.totalLiquid.value

    return when {
        temperature < 10.0 -> "Too cold for most plantings. Consider cold-resistant crops like spinach, carrots, or plant cover crops to protect soil."
        temperature in 10.0..15.0 -> "Good conditions for planting cool-season crops like peas, lettuce, and brassicas. Protect tender seedlings from frost."
        temperature in 15.0..25.0 -> "Ideal planting conditions for a wide range of crops. Good time for tomatoes, peppers, and eggplants."
        temperature in 25.0..30.0 -> "Suitable for heat-loving crops like melons, squash, and okra. Ensure adequate irrigation for new plantings."
        temperature > 30.0 -> "High temperatures may stress new plantings. Focus on drought-tolerant crops like sorghum or millet. Provide shade and extra water if planting."
        precipitation > 20.0 -> "Soil may be too wet for planting. Wait for drier conditions. Consider raised beds or improve drainage."
        precipitation < 5.0 -> "Dry conditions. Ensure irrigation is available before planting. Consider drought-resistant varieties."
        else -> "Good conditions for planting. Ensure proper soil preparation and choose crops suitable for the season."
    }
}

fun getPlantDiseaseRisk(weather: WeatherDto): String {
    val forecast = weather.forecasts.firstOrNull() ?: return "No forecast data available."
    val precipitationProbability = forecast.day.precipitationProbability
    val temperature = forecast.temperature.maximum.value
    val cloudCover = forecast.day.cloudCover

    return when {
        precipitationProbability > 70 && temperature > 20 && cloudCover > 70 ->
            "High risk of fungal diseases, especially for grapes and tomatoes. Monitor crops closely and consider preventive fungicide application. Ensure good air circulation in orchards and vineyards."
        precipitationProbability > 50 && temperature in 15.0..25.0 ->
            "Moderate risk of plant diseases. Watch for early blight in tomatoes and potatoes. Ensure good air circulation in crops and avoid overhead irrigation."
        temperature > 30 && precipitationProbability < 30 ->
            "Low fungal disease risk, but watch for heat stress and sunscald, especially in fruit trees and peppers. Monitor for insect pests which may thrive in hot conditions."
        else -> "Low risk of plant diseases. Maintain regular monitoring practices. Focus on general plant health to improve disease resistance."
    }
}

fun getFruitTreeRecommendation(weather: WeatherDto): String {
    val forecast = weather.forecasts.firstOrNull() ?: return "No forecast data available."
    val temperature = forecast.temperature.maximum.value
    val precipitation = forecast.day.totalLiquid.value
    val windSpeed = forecast.day.wind.speed.value

    val recommendation = StringBuilder()

    when {
        temperature > 35 -> recommendation.append("Extreme heat stress for fruit trees. Increase irrigation and consider shade cloth for sensitive trees like avocados. ")
        temperature in 30.0..35.0 -> recommendation.append("High temperatures may stress fruit trees. Ensure adequate irrigation, especially for citrus and stone fruits. ")
        temperature in 20.0..30.0 -> recommendation.append("Optimal temperature range for most fruit trees. Good conditions for fruit development. ")
        temperature < 10 -> recommendation.append("Cold stress possible. Protect sensitive trees like young citrus. Delay pruning of deciduous fruit trees. ")
    }

    when {
        precipitation > 20 -> recommendation.append("Heavy rain may lead to waterlogging. Ensure good drainage in orchards. Watch for fungal diseases in susceptible fruits like peaches. ")
        precipitation < 5 && temperature > 25 -> recommendation.append("Dry conditions. Prioritize irrigation for fruit trees, especially those with developing fruits. ")
    }

    if (windSpeed > 30) {
        recommendation.append("High winds forecasted. Secure young trees and consider wind breaks for orchards. ")
    }

    recommendation.append("Regular monitoring for pests and diseases is always recommended.")

    return recommendation.toString()
}

fun getCropSelectionRecommendation(weather: WeatherDto): String {
    val forecast = weather.forecasts.firstOrNull() ?: return "No forecast data available."
    val averageTemp = (forecast.temperature.maximum.value + forecast.temperature.minimum.value) / 2
    val precipitation = forecast.day.totalLiquid.value
    val sunHours = forecast.hoursOfSun

    return when {
        averageTemp > 25 && precipitation < 10 && sunHours > 8 ->
            "Hot and dry conditions forecasted. Consider drought-tolerant crops like olives, figs, almonds, and pomegranates. For vegetables, okra, sweet potatoes, and heat-resistant tomato varieties are good choices."

        averageTemp in 20.0..25.0 && precipitation in 10.0..20.0 ->
            "Moderate temperatures with adequate rainfall. Excellent conditions for a wide range of crops including citrus fruits, grapes, tomatoes, peppers, and eggplants. Consider planting herbs like thyme and rosemary which thrive in Mediterranean climates."

        averageTemp in 15.0..20.0 && precipitation > 20 ->
            "Cool temperatures with high rainfall expected. Good conditions for leafy greens, brassicas (cabbage, cauliflower), and root vegetables. Consider planting cover crops to prevent soil erosion."

        averageTemp < 15 ->
            "Cool conditions forecasted. Focus on cold-hardy crops like spinach, kale, carrots, and onions. It's a good time to plant deciduous fruit trees while they're dormant."

        else -> "Varied conditions expected. Diversify your crop selection to mitigate risks. Consider intercropping compatible plants to maximize land use and resilience."
    }
}

