package com.example.jetpackcomposeauthui.data.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeauthui.data.api.ApiService
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GoogleViewModel(private val apiService: ApiService) : ViewModel() {
    private val _googleSignInState = MutableStateFlow<GoogleSignInState>(GoogleSignInState.Idle)
    val googleSignInState: StateFlow<GoogleSignInState> = _googleSignInState

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.e(TAG, "signInResult:failed code=${e.statusCode}")
            _googleSignInState.value = GoogleSignInState.Error("Sign-in failed: ${e.statusCode}")
            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> Log.d(TAG, "Sign-in cancelled")
                GoogleSignInStatusCodes.NETWORK_ERROR -> Log.d(TAG, "Network error")
                else -> Log.d(TAG, "Other error: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch {
            _googleSignInState.value = GoogleSignInState.Loading
            try {
                val response = apiService.googleLogin(idToken)
                if (response.isSuccessful) {
                    response.body()?.let { googleResponse ->
                        _googleSignInState.value = GoogleSignInState.Success(googleResponse)
                    } ?: run {
                        _googleSignInState.value = GoogleSignInState.Error("Empty response body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _googleSignInState.value =
                        GoogleSignInState.Error("Authentication failed: ${response.code()}, Error: $errorBody")
                }
            } catch (e: Exception) {
                _googleSignInState.value = GoogleSignInState.Error("Network error: ${e.message}")
                Log.e(TAG, "Error during Google Sign-In", e)
            }
        }
    }

    companion object {
        private const val TAG = "com.example.jetpackcomposeauthui.data.models.GoogleViewModel"
    }
}
class GoogleViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoogleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoogleViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
sealed class GoogleSignInState {
    object Idle : GoogleSignInState()
    object Loading : GoogleSignInState()
    data class Success(val data: GoogleResponse) : GoogleSignInState()
    data class Error(val message: String) : GoogleSignInState()
}