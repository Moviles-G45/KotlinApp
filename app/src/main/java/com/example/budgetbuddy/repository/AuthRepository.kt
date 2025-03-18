package com.example.budgetbuddy.repository

import com.example.budgetbuddy.model.AuthResponse
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.AuthService  // 🔥 Ahora importa desde services

class AuthRepository {
    private val authService = ApiClient.retrofit.create(AuthService::class.java)

    suspend fun login(user: User): AuthResponse {
        return authService.login(user)
    }

    suspend fun signup(user: User): AuthResponse {
        return authService.signup(user)
    }

    suspend fun recoverPassword(email: String): AuthResponse {
        return authService.recoverPassword(mapOf("email" to email))
    }

    suspend fun resetPassword(token: String, newPassword: String): AuthResponse {
        return authService.resetPassword(mapOf("token" to token, "new_password" to newPassword))
    }

    suspend fun logout(token: String): AuthResponse {
        return authService.logout("Bearer $token")
    }

    suspend fun getCurrentUser(token: String): AuthResponse {
        return authService.getCurrentUser("Bearer $token")
    }
}
