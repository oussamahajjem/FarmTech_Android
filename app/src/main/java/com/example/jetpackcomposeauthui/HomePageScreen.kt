import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpackcomposeauthui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "FarmTech Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                "FarmTechAI",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                "For Tunisian Agriculturists",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0xFF011101)
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate("editProfile")
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_edit_profile), // Icône de modification
                            contentDescription = "Edit Profile",
                            modifier = Modifier.size(24.dp) // Taille ajustée
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                    )
                )
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(getFeatures()) { feature ->
                    FeatureCard(feature = feature, navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(feature: Feature, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = {
            navController.navigate(feature.route)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = feature.backgroundImageResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = feature.iconResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(8.dp)
                )
                Column {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = feature.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        ),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

data class Feature(
    val title: String,
    val description: String,
    val iconResId: Int,
    val backgroundImageResId: Int,
    val route: String
)

fun getFeatures(): List<Feature> {
    return listOf(
        Feature(
            "Weather Forecast",
            "AI-powered local weather predictions",
            R.drawable.ic_weather,
            R.drawable.bg_weather,
            "weather"
        ),
        Feature(
            "Crop Health",
            "Detect diseases and nutrient deficiencies",
            R.drawable.ic_crop,
            R.drawable.bg_crop_health,
            "crop_health"
        ),
        Feature(
            "Recommendations",
            "Optimize your watering schedule",
            R.drawable.ic_water,
            R.drawable.bg_irrigation,
            "Recommendations"
        ),
        Feature(
            "Pest Detection",
            "Identify and manage pests effectively",
            R.drawable.ic_bug,
            R.drawable.bg_pest,
            "pest"
        ),
        Feature(
            "Market Prices",
            "Predict and track agricultural commodity prices",
            R.drawable.ic_market,
            R.drawable.bg_market,
            "market"
        ),
        Feature(
            "Soil Analysis",
            "Get fertilizer recommendations based on soil health",
            R.drawable.ic_soil,
            R.drawable.bg_soil,
            "soil"
        )
    )
}