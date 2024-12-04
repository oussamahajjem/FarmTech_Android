package com.example.jetpackcomposeauthui.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import com.example.jetpackcomposeauthui.data.api.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class VisionViewModel : ViewModel() {
    private val _visionResult = MutableStateFlow<VisionResponse?>(null)
    val visionResult: StateFlow<VisionResponse?> = _visionResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun analyzeImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = createTempFileFromInputStream(inputStream)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val result = RetrofitClient.apiService.analyzeLocalImage(body)
                handleResult(result)
            } catch (e: Exception) {
                _error.value = "Failed to analyze image: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun analyzeImageFromUrl(url: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val visionDto = VisionDto(url)
                val result = RetrofitClient.apiService.analyzeImage(visionDto)
                handleResult(result)
            } catch (e: Exception) {
                _error.value = "Failed to analyze image: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleResult(result: VisionResponse) {
        _visionResult.value = result.copy(
            diseaseDetected = checkForDiseases(result),
            pestsDetected = checkForPests(result)
        )
    }

    private fun createTempFileFromInputStream(inputStream: InputStream?): File {
        val tempFile = File.createTempFile("image_", ".jpg")
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    private fun checkForDiseases(result: VisionResponse): Boolean {
        val diseaseTags = listOf("blight", "rust", "mildew", "rot")
        return result.description.tags.any { it.toLowerCase() in diseaseTags }
    }

    private fun checkForPests(result: VisionResponse): Boolean {
        val pestTags = listOf("insect", "aphid", "caterpillar", "beetle")
        return result.description.tags.any { it.toLowerCase() in pestTags }
    }
}

