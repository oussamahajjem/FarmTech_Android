package com.example.jetpackcomposeauthui.data.models

import java.io.File

data class SignupDto(
    val name: String,
    val email: String,
    val password: String,
    val profilePicture: File? = null
)
