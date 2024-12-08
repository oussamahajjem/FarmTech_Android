package com.example.jetpackcomposeauthui.data.api

import LoginResponse
import com.example.jetpackcomposeauthui.data.models.VisionResponse
import com.example.jetpackcomposeauthui.data.models.ForgotPasswordDto
import com.example.jetpackcomposeauthui.data.models.ForgotPasswordResponse
import com.example.jetpackcomposeauthui.data.models.GoogleResponse
import com.example.jetpackcomposeauthui.data.models.LandInfo
import com.example.jetpackcomposeauthui.data.models.LandInfoWithWeather
import com.example.jetpackcomposeauthui.data.models.LoginDto
import com.example.jetpackcomposeauthui.data.models.MarketPriceResponse
import com.example.jetpackcomposeauthui.data.models.ResetPasswordDto
import com.example.jetpackcomposeauthui.data.models.ResetPasswordResponse
import com.example.jetpackcomposeauthui.data.models.SignUpResponse
import com.example.jetpackcomposeauthui.data.models.UpdateProfileDto
import com.example.jetpackcomposeauthui.data.models.UpdateProfileResponse
import com.example.jetpackcomposeauthui.data.models.UserProfileData
import com.example.jetpackcomposeauthui.data.models.VisionDto
import com.example.jetpackcomposeauthui.data.models.WeatherResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @Multipart
    @POST("auth/signup")
    suspend fun signUp(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part profilePicture: MultipartBody.Part?
    ): Response<SignUpResponse>

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginDto): Response<LoginResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordDto): Response<ForgotPasswordResponse>

    @PUT("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordDto): Response<ResetPasswordResponse>

    @GET("azure-maps/weather")
    suspend fun getWeather(): Response<WeatherResponse>

    @PUT("auth/edit-profile")
    suspend fun editProfile(
        @Header("Authorization") token: String,
        @Body updateProfileDto: UpdateProfileDto
    ): Response<UpdateProfileResponse>

    @GET("auth/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfileData>

    @Multipart
    @POST("vision/analyze")
    suspend fun analyzeLocalImage(@Part file: MultipartBody.Part): VisionResponse

    @POST("vision/analyze")
    suspend fun analyzeImage(@Body visionDto: VisionDto): VisionResponse

    @GET("market-prices")
    suspend fun getMarketPrices(): List<MarketPriceResponse>

    @POST("google-login")
    suspend fun googleLogin(@Body idToken: String): Response<GoogleResponse>

    @GET("land-info")
    suspend fun getAllLandInfo(): List<LandInfo>

    @POST("land-info")
    suspend fun createLandInfo(@Body landInfo: LandInfo): LandInfo

    @GET("land-info/{id}")
    suspend fun getLandInfo(@Path("id") id: String): LandInfo

    @PATCH("land-info/{id}")
    suspend fun updateLandInfo(@Path("id") id: String, @Body landInfo: LandInfo): LandInfo

    @DELETE("land-info/{id}")
    suspend fun deleteLandInfo(@Path("id") id: String): LandInfo

    @GET("land-info/{id}/with-weather")
    suspend fun getLandInfoWithWeather(@Path("id") id: String): LandInfoWithWeather


}



