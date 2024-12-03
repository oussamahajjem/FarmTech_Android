import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.jetpackcomposeauthui.R
import com.example.jetpackcomposeauthui.components.CButton
import com.example.jetpackcomposeauthui.ui.theme.AlegreyaFontFamily
import com.example.jetpackcomposeauthui.ui.theme.AlegreyaSansFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    navController: NavHostController,
    viewModel: SignupViewModel = viewModel()
) {
    val signupState = viewModel.signupState.value
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profilePictureUri = uri
    }

    Surface(
        color = Color(0xFF253334),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
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
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 54.dp)
                        .height(100.dp)
                        .align(Alignment.Start)
                        .offset(x = (-20).dp)
                )

                // Title and Subtitle
                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = AlegreyaFontFamily,
                        fontWeight = FontWeight(500),
                        color = Color.White
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(
                    "Sign up now for free and explore FarmTech.",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = AlegreyaSansFontFamily,
                        color = Color(0xB2FFFFFF)
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                // Optional Profile Picture Selection
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(text = if (profilePictureUri != null) "Change Profile Picture" else "Add Profile Picture (Optional)")
                }

                // Text fields
                CTextField(
                    hint = "Full Name",
                    value = fullName,
                    onValueChange = { fullName = it }
                )

                CTextField(
                    hint = "Email Address",
                    value = email,
                    onValueChange = { email = it }
                )

                CTextField(
                    hint = "Password",
                    value = password,
                    onValueChange = { password = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sign-up button with loading state
                CButton(
                    text = if (signupState.isLoading) "Signing Up..." else "Sign Up",
                    onClick = {
                        if (fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                            viewModel.signUp(context, fullName, email, password, profilePictureUri)
                        }
                    }
                )

                // Show error or success messages
                if (signupState.error != null) {
                    Text(
                        text = signupState.error,
                        color = Color.Red,
                        style = TextStyle(fontSize = 14.sp)
                    )
                }

                if (signupState.success) {
                    Text(
                        text = "Sign-up successful! ${signupState.message}",
                        color = Color.Green,
                        style = TextStyle(fontSize = 16.sp)
                    )
                }

                // Row for existing users to navigate to login
                Row(modifier = Modifier.padding(top = 12.dp, bottom = 52.dp)) {
                    Text(
                        "Already have an account? ",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = AlegreyaSansFontFamily,
                            color = Color.White
                        )
                    )

                    Text(
                        "Sign In",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = AlegreyaSansFontFamily,
                            fontWeight = FontWeight(800),
                            color = Color.White
                        ),
                        modifier = Modifier.clickable {
                            navController.navigate("login")
                        }
                    )
                }
            }
        }
    }
}