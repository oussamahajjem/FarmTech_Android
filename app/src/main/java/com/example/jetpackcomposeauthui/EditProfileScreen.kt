import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.jetpackcomposeauthui.data.models.EditProfileViewModel
import com.example.jetpackcomposeauthui.data.models.ProfileUpdateState
import com.example.jetpackcomposeauthui.data.models.UpdateProfileDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val updateState by viewModel.profileUpdateState.collectAsState()
    val userProfileData by viewModel.userProfileData.collectAsState()

    var name by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile(context)
    }

    LaunchedEffect(userProfileData) {
        userProfileData?.let { data ->
            name = data.name
        }
    }

    LaunchedEffect(updateState) {
        when (updateState) {
            is ProfileUpdateState.Success -> {
                // Show success message and navigate back
                navController.popBackStack()
            }
            is ProfileUpdateState.Unauthorized -> {
                // Token is invalid or expired, navigate to login screen
                navController.navigate("login") {
                    popUpTo("homepage") { inclusive = true }
                }
            }
            else -> {} // Handle other states if needed
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(userProfileData?.profilePictureUrl)
                        .build()
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Old Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                val updateProfileDto = UpdateProfileDto(
                    name = if (name.isNotEmpty()) name else null,
                    oldPassword = if (oldPassword.isNotEmpty()) oldPassword else null,
                    newPassword = if (newPassword.isNotEmpty()) newPassword else null
                )
                viewModel.editProfile(context, updateProfileDto)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Profile")
        }

        when (val state = updateState) {
            is ProfileUpdateState.Loading -> CircularProgressIndicator()
            is ProfileUpdateState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
            is ProfileUpdateState.Success -> Text(state.message, color = MaterialTheme.colorScheme.primary)
            else -> {}
        }
    }
}