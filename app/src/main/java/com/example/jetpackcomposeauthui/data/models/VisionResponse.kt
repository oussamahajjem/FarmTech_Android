package com.example.jetpackcomposeauthui.data.models

data class VisionResponse(
    val categories: List<Category>,
    val description: Description,
    val color: Color,
    var diseaseDetected: Boolean = false,
    var pestsDetected: Boolean = false
)

data class Category(
    val name: String,
    val score: Double
)

data class Description(
    val tags: List<String>,
    val captions: List<Caption>
)

data class Caption(
    val text: String,
    val confidence: Double
)

data class Color(
    val dominantColorForeground: String,
    val dominantColorBackground: String,
    val dominantColors: List<String>,
    val accentColor: String,
    val isBwImg: Boolean
)

