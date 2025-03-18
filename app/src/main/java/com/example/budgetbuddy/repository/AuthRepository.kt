package com.example.budgetbuddy.repository

import com.example.budgetbuddy.model.UserRequest
import com.example.budgetbuddy.model.AuthResponse
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.AuthService  // 🔥 Ahora importa desde services
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(private val authService: AuthService) {

    suspend fun signup(user: UserRequest): Result<AuthResponse> {
        return try {
            val response = authService.signup(user)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                Result.failure(Exception("Error: ${response.errorBody()?.string()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.message}"))
        }
    }

    suspend fun login(user: User): AuthResponse {
        return authService.login(user)
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
