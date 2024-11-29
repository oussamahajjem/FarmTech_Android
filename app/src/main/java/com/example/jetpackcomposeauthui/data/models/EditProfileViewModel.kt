import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeauthui.data.api.ApiService
import com.example.jetpackcomposeauthui.data.models.UpdateProfileDto
import com.example.jetpackcomposeauthui.data.models.UpdateProfileResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class ProfileViewModel(private val apiService: ApiService) : ViewModel() {

    fun updateProfile(
        token: String,
        name: String?,
        oldPassword: String?,
        newPassword: String?,
        onSuccess: (Response<UpdateProfileResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = UpdateProfileDto(
                    name = name,
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )
                val response = apiService.updateProfile("Bearer $token", request)

                if (response.isSuccessful) {
                    onSuccess(response)
                } else {
                    onError("Erreur : ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Exception : ${e.message}")
            }
        }
    }
}
