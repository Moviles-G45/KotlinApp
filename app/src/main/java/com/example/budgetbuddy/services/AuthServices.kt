package com.example.budgetbuddy.services

import com.example.budgetbuddy.model.AuthResponse
import com.example.budgetbuddy.model.UserLogin
import com.example.budgetbuddy.model.UserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("/auth/login")
    suspend fun login(@Body userLogin: UserLogin): AuthResponse

    @POST("/auth/signup")
    suspend fun signup(@Body user: UserRequest): Response<AuthResponse>

    @POST("/auth/recover")
    suspend fun recoverPassword(@Query("email") email: String): Response<AuthResponse>

    @POST("/auth/reset")
    suspend fun resetPassword(@Body resetData: Map<String, String>): Response<AuthResponse>

    @POST("/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<AuthResponse>

    @GET("/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<AuthResponse>
}
