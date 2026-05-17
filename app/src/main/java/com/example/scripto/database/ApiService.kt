package com.example.scripto.database

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path

data class ForgotPasswordRequest(
    val email: String)
data class VerifyOtpRequest(
    val email: String?,
    val code: String)
data class ResetPasswordRequest(
    val email: String,
    val newPassword: String)

data class LoginResponse(
    val token: String
)

data class TextRequest(
    val title: String,
    val content: String
)

data class UserText(
    val id: Int,
    val user_id: Int,
    val title: String,
    val content: String,
    val created_at: String
)

data class OcrResponse(
    val raw_text: String,
    val corrected_text: String
)

data class RegisterResponse(
    val userId: Int
)
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

    @POST("auth/forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ApiResponse>

    @POST("auth/verify-otp")
    fun verifyOtp(@Body request: VerifyOtpRequest): Call<ApiResponse>

    @POST("auth/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<ApiResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/google")
    fun googleLogin(@Body body: Map<String, String>): Call<LoginResponse>

    @GET("profile")
    fun getProfile(
        @Header("Authorization") token: String
    ): Call<ApiResponse>

    @Multipart
    @POST("auth/ocr")
    suspend fun uploadOcrImage(
        @Part file: MultipartBody.Part
    ): OcrResponse

    // ИСПРАВЛЕННЫЕ РОУТЫ ДЛЯ РАБОТЫ С АРХИВОМ:
    @GET("auth/api/texts")
    suspend fun getTexts(
        @Header("Authorization") token: String
    ): retrofit2.Response<List<UserText>>

    @POST("auth/api/texts")
    suspend fun createText(
        @Header("Authorization") token: String,
        @Body body: TextRequest
    ): retrofit2.Response<okhttp3.ResponseBody>

    @DELETE("auth/api/texts/{id}")
    suspend fun deleteText(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): retrofit2.Response<okhttp3.ResponseBody>
}
