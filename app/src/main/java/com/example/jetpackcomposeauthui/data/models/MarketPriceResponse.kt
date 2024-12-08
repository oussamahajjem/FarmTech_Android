package com.example.jetpackcomposeauthui.data.models
import com.google.gson.annotations.SerializedName
import java.util.Date

data class MarketPriceResponse(
    val id: String,
    val productName: String,
    val price: Double,
    val predictedPrice: Double?,
    val unit: String,
    @SerializedName("date")
    val date: Date,
    val location: String?,
    val source: String?,
    @SerializedName("imageUrl")
    val imageUrl: String? // Ensure this matches the API response
)
