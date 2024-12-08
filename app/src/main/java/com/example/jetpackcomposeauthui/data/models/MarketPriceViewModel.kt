package com.example.jetpackcomposeauthui.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeauthui.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarketPriceViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService
    private val baseUrl = "http://192.168.1.11:3000" // Use your local network IP address

    private val _marketPrices = MutableStateFlow<List<MarketPriceResponse>>(emptyList())
    val marketPrices: StateFlow<List<MarketPriceResponse>> = _marketPrices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchMarketPrices()
    }

    private fun fetchMarketPrices() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val prices = apiService.getMarketPrices()
                _marketPrices.value = prices.map { price ->
                    price.copy(imageUrl = constructFullImageUrl(price.imageUrl))
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch market prices: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun constructFullImageUrl(relativeUrl: String?): String {
        return relativeUrl?.let { url ->
            if (url.startsWith("http")) url else "$baseUrl$url"
        } ?: ""
    }

    fun retryFetchMarketPrices() {
        fetchMarketPrices()
    }
}