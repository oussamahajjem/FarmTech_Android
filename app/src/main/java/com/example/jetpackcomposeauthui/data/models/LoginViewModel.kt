package com.example.jetpackcomposeauthui.data.models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeauthui.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginViewModel : ViewModel() {

    private val _loginState = mutableStateOf(LoginState())


    val loginState: State<LoginState> = _loginState

    private val _forgotPasswordState = mutableStateOf(ForgotPasswordState())
    private fun storeAccessToken(appContext: Context, token: String) {
        val sharedPreferences = appContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("access_token", token).apply()
    }



    fun login(context: Context, email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState(isLoading = true)

            try {
                // Appeler l'API pour se connecter
                val response = RetrofitClient.apiService.login(LoginDto(email, password))

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && !loginResponse.accessToken.isNullOrEmpty()) {
                        // Stocker l'access token
                        storeAccessToken(context, loginResponse.accessToken)
                        Log.d("LoginViewModel", "Token stored: ${loginResponse.accessToken}")

                        // Mettre à jour l'état
                        _loginState.value = LoginState(success = true, token = loginResponse.accessToken)
                    } else {
                        Log.e("LoginViewModel", "Login successful, but accessToken is null or empty")
                        _loginState.value = LoginState(error = "No accessToken received from server")
                    }
                } else {
                    Log.e("LoginViewModel", "Login failed: ${response.code()} - ${response.message()}")
                    _loginState.value = LoginState(error = "Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error: ${e.message}")
                _loginState.value = LoginState(error = "Error: ${e.message}")
            }
        }
    }


    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordState.value = ForgotPasswordState(isLoading = true)
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.forgotPassword(ForgotPasswordDto(email))
                }

                Log.d("ForgotPassword", "Response code: ${response.code()}")
                Log.d("ForgotPassword", "Response body: ${response.body()}")

                if (response.isSuccessful) {
                    _forgotPasswordState.value = ForgotPasswordState(
                        success = true,
                        message = response.body()?.message ?: "Reset link sent successfully"
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ForgotPassword", "Error body: $errorBody")
                    _forgotPasswordState.value = ForgotPasswordState(
                        success = false,
                        error = "Error: ${response.message()} (${response.code()})"
                    )
                }
            } catch (e: Exception) {
                Log.e("ForgotPassword", "Exception: ${e.message}", e)
                _forgotPasswordState.value = ForgotPasswordState(
                    success = false,
                    error = "Error: ${e.message}"
                )
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

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val message: String? = null,
    val error: String? = null
)
