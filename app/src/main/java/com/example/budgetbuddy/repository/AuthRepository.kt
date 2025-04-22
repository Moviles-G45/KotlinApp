package com.example.budgetbuddy.repository

import com.example.budgetbuddy.model.UserRequest
import com.example.budgetbuddy.model.AuthResponse
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.model.UserLogin
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.AuthService  // 🔥 Ahora importa desde services
import retrofit2.HttpException
import retrofit2.Response
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

    suspend fun login(userLogin: UserLogin): Result<AuthResponse> {
        return try {
            val response = authService.login(userLogin) //Pasar instancia correcta

            Result.success(response) // Si todo va bien, devolver AuthResponse envuelto en Result
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}")) // Manejo de error de red
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.message}")) //  Manejo de error HTTP
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}")) // Otros errores
        }
    }

    suspend fun recoverPassword(email: String): Response<AuthResponse> {
        return authService.recoverPassword(email) //
    }

    suspend fun resetPassword(token: String, newPassword: String): Response<AuthResponse> {
        return authService.resetPassword(mapOf("token" to token, "new_password" to newPassword))
    }

    suspend fun logout(token: String): Response<AuthResponse> {
        return authService.logout("Bearer $token")
    }

    suspend fun getCurrentUser(token: String): Response<AuthResponse> {
        return authService.getCurrentUser("Bearer $token")
    }
}
