package com.example.jetpackcomposeauthui.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeauthui.data.api.RetrofitClient
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LandInfoViewModel : ViewModel() {
    private val _landInfoState = MutableStateFlow<LandInfoState>(LandInfoState.Initial)
    val landInfoState: StateFlow<LandInfoState> = _landInfoState

    private val _landInfoList = MutableStateFlow<List<LandInfo>>(emptyList())
    val landInfoList: StateFlow<List<LandInfo>> = _landInfoList

    fun createLandInfo(area: Double, soilType: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _landInfoState.value = LandInfoState.Loading
            try {
                val landInfo = LandInfo(
                    area = area,
                    soilType = soilType,
                    latitude = latitude,
                    longitude = longitude
                )
                val createdLandInfo = RetrofitClient.apiService.createLandInfo(landInfo)
                _landInfoList.value += createdLandInfo
                _landInfoState.value = LandInfoState.Success(createdLandInfo)
            } catch (e: HttpException) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "HTTP Error occurred")
            } catch (e: Exception) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getLandInfoList() {
        viewModelScope.launch {
            _landInfoState.value = LandInfoState.Loading
            try {
                val landInfoList = RetrofitClient.apiService.getAllLandInfo()
                _landInfoList.value = landInfoList
                _landInfoState.value = LandInfoState.Success(landInfoList)
            } catch (e: HttpException) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "HTTP Error occurred")
            } catch (e: Exception) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getLandInfo(id: String) {
        viewModelScope.launch {
            _landInfoState.value = LandInfoState.Loading
            try {
                val landInfo = RetrofitClient.apiService.getLandInfo(id)
                _landInfoState.value = LandInfoState.Success(landInfo)
            } catch (e: HttpException) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "HTTP Error occurred")
            } catch (e: Exception) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun updateLandInfo(id: String, area: Double, soilType: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _landInfoState.value = LandInfoState.Loading
            try {
                val updatedLandInfo = LandInfo(id, area, soilType, latitude, longitude)
                val result = RetrofitClient.apiService.updateLandInfo(id, updatedLandInfo)
                _landInfoList.value = _landInfoList.value.map { if (it.id == id) result else it }
                _landInfoState.value = LandInfoState.Success(result)
            } catch (e: HttpException) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "HTTP Error occurred")
            } catch (e: Exception) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun deleteLandInfo(id: String) {
        viewModelScope.launch {
            _landInfoState.value = LandInfoState.Loading
            try {
                RetrofitClient.apiService.deleteLandInfo(id)
                _landInfoList.value = _landInfoList.value.filter { it.id != id }
                _landInfoState.value = LandInfoState.Success("Land info deleted successfully")
            } catch (e: HttpException) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "HTTP Error occurred")
            } catch (e: Exception) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getLandInfoWithWeather(id: String) {
        viewModelScope.launch {
            _landInfoState.value = LandInfoState.Loading
            try {
                val landInfoWithWeather = RetrofitClient.apiService.getLandInfoWithWeather(id)
                _landInfoState.value = LandInfoState.Success(landInfoWithWeather)
            } catch (e: HttpException) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "HTTP Error occurred")
            } catch (e: Exception) {
                _landInfoState.value = LandInfoState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class LandInfoState {
    object Initial : LandInfoState()
    object Loading : LandInfoState()
    data class Success(val data: Any) : LandInfoState()
    data class Error(val message: String) : LandInfoState()
}

