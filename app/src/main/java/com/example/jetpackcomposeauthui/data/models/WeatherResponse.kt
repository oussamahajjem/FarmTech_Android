package com.example.jetpackcomposeauthui.data.models

data class WeatherResponse(
    val summary: SummaryDto,
    val forecasts: List<ForecastDto>
)
