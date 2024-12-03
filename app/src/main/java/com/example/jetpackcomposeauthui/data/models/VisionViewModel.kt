package com.example.jetpackcomposeauthui.data.models

import VisionResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeauthui.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VisionViewModel : ViewModel() {
    private val _visionResult = MutableStateFlow<VisionResponse?>(null)
    val visionResult: StateFlow<VisionResponse?> = _visionResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun analyzeImage(imageUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.apiService.analyzeImage(VisionDto(imageUrl))
                if (response.isSuccessful) {
                    _visionResult.value = response.body()
                } else {
                    _error.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to analyze image: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}