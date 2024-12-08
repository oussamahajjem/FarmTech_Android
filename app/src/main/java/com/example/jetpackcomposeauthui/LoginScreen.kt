package com.example.jetpackcomposeauthui

import CTextField
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeauthui.components.CButton
import com.example.jetpackcomposeauthui.components.DontHaveAccountRow
import com.example.jetpackcomposeauthui.components.ForgotPasswordRow
import com.example.jetpackcomposeauthui.ui.theme.AlegreyaFontFamily
import com.example.jetpackcomposeauthui.ui.theme.AlegreyaSansFontFamily
import com.example.jetpackcomposeauthui.data.models.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel()
) {
    val loginState by viewModel.loginState

    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var emailResetPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isResetPasswordLoading by remember { mutableStateOf(false) }
    var resetPasswordError by remember { mutableStateOf<String?>(null) }
    var resetPasswordSuccess by remember { mutableStateOf(false) }

    Surface(
        color = Color(0xFF253334),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg1),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .align(Alignment.BottomCenter)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 54.dp)
                        .height(100.dp)
                        .align(Alignment.Start)
                        .offset(x = (-20).dp)
                )

                Text(
                    text = "Sign In",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = AlegreyaFontFamily,
                        fontWeight = FontWeight(500),
                        color = Color.White
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(
                    "Sign In now to access to our advices.",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = AlegreyaSansFontFamily,
                        color = Color(0xB2FFFFFF)
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                CTextField(
                    hint = "Email Address",
                    value = email,
                    onValueChange = { email = it }
                )

                CTextField(
                    hint = "Password",
                    value = password,
                    onValueChange = { password = it },
                    isPassword = true
                )

                ForgotPasswordRow(
                    onForgotPasswordTap = {
                        showResetPasswordDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(2.dp))
                val context = LocalContext.current

                CButton(
                    text = "Sign In",
                    onClick = {
                        viewModel.login(context, email, password)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gmail icon for Google Sign-In
                Image(
                    painter = painterResource(id = R.drawable.gmail),
                    contentDescription = "Sign in with Google",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            navController.navigate("google")
                        }
                )

                if (loginState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                loginState.error?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = TextStyle(fontSize = 16.sp),
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                if (loginState.success) {
                    showToast(LocalContext.current, "Login successful!!")
                    LaunchedEffect(Unit) {
                        navController.navigate("homepage")
                    }
                }

                DontHaveAccountRow(
                    onSignupTap = {
                        navController.navigate("signup")
                    }
                )
            }
        }

        if (showResetPasswordDialog) {
            AlertDialog(
                onDismissRequest = {
                    showResetPasswordDialog = false
                    emailResetPassword = ""
                    resetPasswordError = null
                    resetPasswordSuccess = false
                },
                title = {
                    Text(
                        text = "Reset Password",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Enter your email address and we'll send you instructions to reset your password.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        OutlinedTextField(
                            value = emailResetPassword,
                            onValueChange = {
                                emailResetPassword = it
                                resetPasswordError = null
                            },
                            label = { Text("Email", color = Color.White) },
                            placeholder = { Text("Enter your email", color = Color.White.copy(alpha = 0.5f)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isResetPasswordLoading,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                cursorColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            },
                            isError = resetPasswordError != null
                        )
                        resetPasswordError?.let {
                            Text(
                                text = it,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (resetPasswordSuccess) {
                            Text(
                                text = "Password reset instructions have been sent to your email.",
                                color = Color.Green,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (emailResetPassword.isBlank()) {
                                resetPasswordError = "Please enter your email address"
                            } else {
                                isResetPasswordLoading = true
                                resetPasswordError = null
                                viewModel.forgotPassword(emailResetPassword)
                                // Simulating API call
                                Handler(Looper.getMainLooper()).postDelayed({
                                    isResetPasswordLoading = false
                                    resetPasswordSuccess = true
                                }, 2000)
                            }
                        },
                        enabled = !isResetPasswordLoading && emailResetPassword.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF253334)
                        )
                    ) {
                        if (isResetPasswordLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF253334)
                            )
                        } else {
                            Text("Reset Password")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showResetPasswordDialog = false
                            emailResetPassword = ""
                            resetPasswordError = null
                            resetPasswordSuccess = false
                        },
                        enabled = !isResetPasswordLoading,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = Color(0xFF253334),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}