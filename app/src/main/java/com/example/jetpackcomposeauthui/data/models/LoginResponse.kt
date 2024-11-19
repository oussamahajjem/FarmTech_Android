package com.example.jetpackcomposeauthui.data.models

data class LoginResponse(
    val message: String,
    val token: String? = null
)
