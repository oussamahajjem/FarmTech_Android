package com.example.jetpackcomposeauthui.data.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeauthui.data.api.RetrofitClient
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {

    private val _signupState = mutableStateOf(SignupState())
    val signupState: State<SignupState> = _signupState

    fun signUp(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _signupState.value = SignupState(isLoading = true)
            try {
                val response = RetrofitClient.apiService.signUp(
                    SignupDto(name = fullName, email = email, password = password)
                )
                if (response.isSuccessful) {
                    _signupState.value = SignupState(success = true, message = response.body()?.message.toString())
                } else {
                    _signupState.value = SignupState(error = "Sign-up failed")
                }
            } catch (e: Exception) {
                _signupState.value = SignupState(error = "Error: ${e.message}")
            }
        }
    }
}

data class SignupState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val message: String = "",
    val error: String? = null
)
