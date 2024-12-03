import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeauthui.data.api.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class SignupViewModel : ViewModel() {

    private val _signupState = mutableStateOf(SignupState())
    val signupState: State<SignupState> = _signupState

    fun signUp(context: Context, fullName: String, email: String, password: String, profilePictureUri: Uri?) {
        viewModelScope.launch {
            _signupState.value = SignupState(isLoading = true)
            try {
                val nameRequestBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailRequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val passwordRequestBody = password.toRequestBody("text/plain".toMediaTypeOrNull())

                var profilePicturePart: MultipartBody.Part? = null
                profilePictureUri?.let { uri ->
                    val file = File(context.cacheDir, "profile_picture")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    profilePicturePart = MultipartBody.Part.createFormData("profilePicture", file.name, requestBody)
                }

                val response = RetrofitClient.apiService.signUp(
                    name = nameRequestBody,
                    email = emailRequestBody,
                    password = passwordRequestBody,
                    profilePicture = profilePicturePart
                )

                if (response.isSuccessful) {
                    _signupState.value = SignupState(success = true, message = response.body()?.message.toString())
                } else {
                    _signupState.value = SignupState(error = "Sign-up failed: ${response.errorBody()?.string()}")
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