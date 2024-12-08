package com.example.jetpackcomposeauthui.data.models

import java.util.Date

data class MarketPriceDto(
    val productName: String,
    val price: Double,
    val unit: String,
    val date: Date,
    val location: String?,
    val source: String?,
    val imageUrl: String?
)

