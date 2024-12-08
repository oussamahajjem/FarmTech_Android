import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.util.Log
import com.example.jetpackcomposeauthui.MainActivity
import com.example.jetpackcomposeauthui.data.models.GoogleSignInState
import com.example.jetpackcomposeauthui.data.models.GoogleViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun GoogleScreen(
    navController: NavController,
    viewModel: GoogleViewModel,
    googleSignInClient: GoogleSignInClient
) {
    val googleSignInState by viewModel.googleSignInState.collectAsState()

    LaunchedEffect(googleSignInState) {
        when (googleSignInState) {
            is GoogleSignInState.Success -> {
                Log.d("GoogleScreen", "Sign-in successful")
                navController.navigate("homepage")
            }
            is GoogleSignInState.Error -> {
                Log.e("GoogleScreen", "Sign-in error: ${(googleSignInState as GoogleSignInState.Error).message}")
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = googleSignInState) {
            is GoogleSignInState.Idle -> {
                Button(
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        (navController.context as MainActivity).googleSignInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign in with Google")
                }
            }
            is GoogleSignInState.Loading -> CircularProgressIndicator()
            is GoogleSignInState.Success -> {
                Text("Welcome, ${state.data.name}!")

            }
            is GoogleSignInState.Error -> {
                Text(
                    "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        (navController.context as MainActivity).googleSignInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Try Again")
                }
            }

            else -> {}
        }
    }
}