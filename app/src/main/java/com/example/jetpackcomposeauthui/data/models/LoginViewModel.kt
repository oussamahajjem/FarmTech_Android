package com.example.jetpackcomposeauthui.data.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeauthui.data.api.RetrofitClient
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val _loginState = mutableStateOf(LoginState())


    val loginState: State<LoginState> = _loginState


    fun login(email: String, password: String) {
        viewModelScope.launch {

            _loginState.value = LoginState(isLoading = true)

            try {

                val response = RetrofitClient.apiService.login(LoginDto(email, password))


                if (response.isSuccessful) {

                    _loginState.value = LoginState(success = true, token = response.body()?.token)
                } else {

                    _loginState.value = LoginState(error = "Login failed: ${response.message()}")
                }
            } catch (e: Exception) {

                _loginState.value = LoginState(error = "Error: ${e.message}")
            }
        }
    }
}


data class LoginState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val token: String? = null,
    val error: String? = null
)
