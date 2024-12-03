package com.example.jetpackcomposeauthui.data.models

data class UpdateProfileDto(
    val name: String? = null,
    val oldPassword: String? = null,
    val newPassword: String? = null
)
