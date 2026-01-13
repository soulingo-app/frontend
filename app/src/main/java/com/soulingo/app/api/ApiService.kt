package com.soulingo.app.api

import com.soulingo.app.data.model.Lesson
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api/v1/lessons")
    suspend fun getLessons(): Response<List<Lesson>>

    @GET("api/v1/lessons/{id}")
    suspend fun getLesson(@Path("id") id: Int): Response<Lesson>

    // Auth endpoints
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/v1/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserResponse>

    // User endpoints
    @PUT("api/v1/users/recording")
    suspend fun updateRecording(
        @Header("Authorization") token: String,
        @Body request: UpdateRecordingRequest
    ): Response<UserResponse>
}