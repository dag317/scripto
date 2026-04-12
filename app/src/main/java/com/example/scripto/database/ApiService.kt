package com.example.scripto.database

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class ForgotPasswordRequest(
    val email: String)
data class VerifyOtpRequest(
    val email: String?,
    val code: String)
data class ResetPasswordRequest(
    val email: String,
    val newPassword: String)

data class ApiResponse(
    val message: String? = null,
    val error: String? = null,
    val success: Boolean? = null
)
data class RegisterRequest(
    val email: String,
    val password: String
)
data class LoginRequest(
    val email: String,
    val password: String
)

interface ApiService {

    @POST("/auth/forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ApiResponse>

    @POST("/auth/verify-otp")
    fun verifyOtp(@Body request: VerifyOtpRequest): Call<ApiResponse>

    @POST("/auth/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<ApiResponse>

    @POST("/auth/register")
    fun register(@Body request: RegisterRequest): Call<ApiResponse>

    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Call<ApiResponse>
}