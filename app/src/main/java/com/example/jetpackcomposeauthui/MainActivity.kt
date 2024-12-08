package com.example.jetpackcomposeauthui

import GoogleScreen
import HomePageScreen
import SignupScreen
import VisionScreen
import VisionViewModel
import WeatherScreen
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeauthui.data.api.ApiService
import com.example.jetpackcomposeauthui.data.models.GoogleViewModel
import com.example.jetpackcomposeauthui.data.models.GoogleViewModelFactory
import com.example.jetpackcomposeauthui.data.models.LandInfoViewModel
import com.example.jetpackcomposeauthui.data.models.MarketPriceViewModel
import com.example.jetpackcomposeauthui.data.models.WeatherViewModel
import com.example.jetpackcomposeauthui.ui.theme.JetpackComposeAuthUITheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleViewModel: GoogleViewModel
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ApiService
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.20.10.5:3000/") // Replace with your actual base UR  L
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize ViewModel using ViewModelProvider
        googleViewModel = ViewModelProvider(this, GoogleViewModelFactory(apiService))
            .get(GoogleViewModel::class.java)

        // Set up the ActivityResultLauncher for Google Sign-In
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "Google Sign-In successful. ID: ${account.id}")
                googleViewModel.handleSignInResult(task)
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed", e)
                googleViewModel.handleSignInResult(task)
            }
        }

        setContent {
            JetpackComposeAuthUITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationView()
                }
            }
        }
    }

    @Composable
    fun NavigationView() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "welcome") {
            composable("welcome") { WelcomeScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignupScreen(navController) }
            composable("homepage") { HomePageScreen(navController) }
            composable("weather") {
                val weatherViewModel: WeatherViewModel = viewModel()
                WeatherScreen(
                    navController = navController,
                    viewModel = weatherViewModel,
                    onBackClick = { navController.navigateUp() }
                )
            }
            composable("editprofile") {
                EditProfileScreen(navController = navController)
            }
            composable("crop_health") {
                val visionViewModel: VisionViewModel = viewModel()
                VisionScreen(navController, visionViewModel)
            }
            composable("recommendations") {
                val weatherViewModel: WeatherViewModel = viewModel()
                RecommendationsScreen(navController = navController, viewModel = weatherViewModel)
            }
            composable("market") {
                val marketPriceViewModel: MarketPriceViewModel = viewModel()
                MarketPriceScreen(navController, marketPriceViewModel)
            }
            composable("soil") {
                val landInfoViewModel: LandInfoViewModel = viewModel()
                LandInfoScreen(navController, landInfoViewModel)
            }
            composable("google") {
                GoogleScreen(
                    navController = navController,
                    viewModel = googleViewModel,
                    googleSignInClient = googleSignInClient
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            // User is already signed in, update UI accordingly
            googleViewModel.handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(intent))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Sign out when the activity is destroyed to prevent automatic sign-in on next launch
        googleSignInClient.signOut()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}