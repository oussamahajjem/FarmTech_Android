package com.example.jetpackcomposeauthui.data.models

data class GoogleResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val name: String,
    val email: String,
    val profilePicture: String,
    val errorMessage: String,
)
