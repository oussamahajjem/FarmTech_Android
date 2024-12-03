package com.example.jetpackcomposeauthui.data.models

data class ResetPasswordDto(
    val resetToken: String,
    val newPassword: String
)
