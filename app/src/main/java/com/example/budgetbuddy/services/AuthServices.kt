package com.example.budgetbuddy.services  // Ahora está en services

import com.example.budgetbuddy.model.AuthResponse
import com.example.budgetbuddy.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/login")
    suspend fun login(@Body user: User): AuthResponse

    @POST("/auth/signup")
    suspend fun signup(@Body user: User): AuthResponse

    @POST("/auth/recover")
    suspend fun recoverPassword(@Body email: Map<String, String>): AuthResponse

    @POST("/auth/reset")
    suspend fun resetPassword(@Body resetData: Map<String, String>): AuthResponse

    @POST("/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): AuthResponse

    @GET("/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): AuthResponse
}

