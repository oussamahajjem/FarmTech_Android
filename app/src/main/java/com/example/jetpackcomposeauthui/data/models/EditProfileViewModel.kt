package com.example.jetpackcomposeauthui.data.models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.jetpackcomposeauthui.data.api.RetrofitClient

class EditProfileViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService

    private val _profileUpdateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val profileUpdateState: StateFlow<ProfileUpdateState> = _profileUpdateState

    private val _userProfileData = MutableStateFlow<UserProfileData?>(null)
    val userProfileData: StateFlow<UserProfileData?> = _userProfileData

    fun fetchUserProfile(context: Context) {
        viewModelScope.launch {
            try {
                val token = getToken(context)
                if (token.isNullOrEmpty()) {
                    _profileUpdateState.value = ProfileUpdateState.Error("No token found. Please log in again.")
                    return@launch
                }
                val response = apiService.getUserProfile("Bearer $token")
                if (response.isSuccessful) {
                    _userProfileData.value = response.body()
                } else {
                    _profileUpdateState.value = ProfileUpdateState.Error("Failed to fetch user profile")
                }
            } catch (e: Exception) {
                _profileUpdateState.value = ProfileUpdateState.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    fun editProfile(context: Context, updateProfileDto: UpdateProfileDto) {
        viewModelScope.launch {
            _profileUpdateState.value = ProfileUpdateState.Loading
            val token = getToken(context)
            if (token.isNullOrEmpty()) {
                _profileUpdateState.value = ProfileUpdateState.Error("No token found. Please log in again.")
                return@launch
            }
            try {
                val response = apiService.editProfile("Bearer $token", updateProfileDto)
                if (response.isSuccessful) {
                    _profileUpdateState.value = ProfileUpdateState.Success("Profile updated successfully")
                    fetchUserProfile(context) // Refresh user data after successful update
                } else {
                    when (response.code()) {
                        401 -> _profileUpdateState.value = ProfileUpdateState.Unauthorized
                        else -> _profileUpdateState.value = ProfileUpdateState.Error("Error: ${response.code()} - ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _profileUpdateState.value = ProfileUpdateState.Error("Exception: ${e.localizedMessage}")
            }
        }
    }

    private fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }
}

sealed class ProfileUpdateState {
    object Idle : ProfileUpdateState()
    object Loading : ProfileUpdateState()
    data class Success(val message: String) : ProfileUpdateState()
    data class Error(val message: String) : ProfileUpdateState()
    object Unauthorized : ProfileUpdateState()
}