package com.example.jetpackcomposeauthui.data.models

data class LandInfo(
    val id: String? = null, // Changed from UUID to nullable String
    val area: Double,
    val soilType: String,
    val latitude: Double,
    val longitude: Double
)


