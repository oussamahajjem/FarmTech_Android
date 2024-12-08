package com.example.jetpackcomposeauthui

import CTextField
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.foundation.Image
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.jetpackcomposeauthui.components.CButton
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


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF7C9A92),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
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

            CTextField(
                hint = "Name",
                value = name,
                onValueChange = { name = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CTextField(
                hint = "Old Password",
                value = oldPassword,
                onValueChange = { oldPassword = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            CTextField(
                hint = "New Password",
                value = newPassword,
                onValueChange = { newPassword = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            CButton(
                onClick = {
                    val updateProfileDto = UpdateProfileDto(
                        name = if (name.isNotEmpty()) name else null,
                        oldPassword = if (oldPassword.isNotEmpty()) oldPassword else null,
                        newPassword = if (newPassword.isNotEmpty()) newPassword else null
                    )
                    viewModel.editProfile(context, updateProfileDto)
                },
                text = "Update Profile"
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = updateState) {
                is ProfileUpdateState.Loading -> CircularProgressIndicator(color = Color(0xFF7C9A92))
                is ProfileUpdateState.Error -> Text(state.message, color = Color.Red)
                is ProfileUpdateState.Success -> Text(state.message, color = Color(0xFF7C9A92))
                else -> {}
            }
        }
    }
}

