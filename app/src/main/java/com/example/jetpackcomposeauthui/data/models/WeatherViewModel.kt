package com.example.jetpackcomposeauthui.data.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeauthui.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState

    fun fetchWeather() {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            try {
                Log.d("WeatherViewModel", "Fetching weather data...")

                // Appel à l'API via Retrofit
                val response = RetrofitClient.apiService.getWeather()

                Log.d("WeatherViewModel", "Response received: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    // Récupération du corps de la réponse
                    val weatherResponse = response.body()

                    // Vérification et extraction de données
                    if (weatherResponse != null && weatherResponse.forecasts.isNotEmpty()) {
                        // Créer un WeatherDto avec le résumé et la liste de prévisions
                        val weatherData = WeatherDto(
                            summary = weatherResponse.summary,
                            forecasts = weatherResponse.forecasts
                        )

                        Log.d("WeatherViewModel", "Weather data: $weatherData")

                        // Mise à jour de l'état avec succès
                        _weatherState.value = WeatherState.Success(weatherData)
                    } else {
                        // Erreur si aucune donnée n'est disponible
                        _weatherState.value = WeatherState.Error("No weather data available")
                    }
                } else {
                    // Erreur en cas de réponse non réussie
                    _weatherState.value = WeatherState.Error("Failed to fetch weather data: ${response.code()}")
                }
            } catch (e: Exception) {
                // Gestion des exceptions
                Log.e("WeatherViewModel", "Error fetching weather data", e)
                _weatherState.value = WeatherState.Error("An error occurred: ${e.message}")
            }
        }
    }


}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val weather: WeatherDto) : WeatherState()
    data class Error(val message: String) : WeatherState()
}