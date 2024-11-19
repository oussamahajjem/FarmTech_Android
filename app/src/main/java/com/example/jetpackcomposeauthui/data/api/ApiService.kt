package com.example.jetpackcomposeauthui.data.api

import com.example.jetpackcomposeauthui.data.models.LoginDto
import com.example.jetpackcomposeauthui.data.models.LoginResponse
import com.example.jetpackcomposeauthui.data.models.SignUpResponse
import com.example.jetpackcomposeauthui.data.models.SignupDto
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("signup")
    suspend fun signUp(@Body signupData: SignupDto): Response<SignUpResponse>


    @POST("login")
    suspend fun login(@Body credentials: LoginDto): Response<LoginResponse>
}
