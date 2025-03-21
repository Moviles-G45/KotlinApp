package com.example.budgetbuddy.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.AuthResponse
import com.example.budgetbuddy.model.UserLogin
import com.example.budgetbuddy.model.UserRequest
import com.example.budgetbuddy.repository.AuthRepository
import com.example.budgetbuddy.storage.SessionManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import retrofit2.Response


sealed class AuthState {
    object Idle : AuthState()  // Estado inicial
    object Loading : AuthState()
    data class Success(val response: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
    object Unauthenticated : AuthState() // Estado para usuarios no autenticados
}

class AuthViewModel(
    private val repository: AuthRepository,
    context: Context
) : ViewModel() {

    private val sessionManager = SessionManager(context)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState // UI observará este estado

    init {
        val persistedToken = sessionManager.fetchToken()
        if (persistedToken != null) {
            sendTokenToBackend(persistedToken)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    // 🔹 SIGN UP
    fun signup(user: UserRequest) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            repository.signup(user).fold(
                onSuccess = {
                    login(user.email, user.password)
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Error desconocido")
                }
            )
        }
    }


    // LOGIN
    fun login(email: String, password: String) {

        // Validación de campos vacíos
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Los campos no pueden estar vacíos")
            return
        }

        _authState.value = AuthState.Loading // Mostrar loading
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firebase.auth.currentUser?.getIdToken(true)
                        ?.addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful) {
                                val idToken = tokenTask.result?.token
                                if (idToken != null) {
                                    sessionManager.saveToken(idToken)
                                    sendTokenToBackend(idToken)
                                } else {
                                    _authState.value = AuthState.Error("No se pudo obtener el token")
                                }
                            } else {
                                _authState.value = AuthState.Error(tokenTask.exception?.localizedMessage ?: "Error en token")
                            }
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Error en login")
                }
            }
    }

    private fun sendTokenToBackend(idToken: String) {
        viewModelScope.launch {
            try {
                val result = repository.login(UserLogin(idToken)) // `result` es un Result<AuthResponse>

                _authState.value = result.fold(
                    onSuccess = { AuthState.Success(it) }, // Extraemos el valor si es éxito
                    onFailure = { AuthState.Error(it.localizedMessage ?: "Login failed") } //  Manejo de error
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Login failed")
            }
        }
    }

    fun getPersistedToken(): String? {
        return sessionManager.fetchToken()
    }

    // LOGOUT
    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading //  Indicar que se está cerrando sesión
            try {
                Firebase.auth.signOut() // Cierra sesión en Firebase
                sessionManager.clearToken()
                _authState.value = AuthState.Unauthenticated // Usuario desautenticado
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error en logout")
            }
        }
    }

    fun recoverPassword(email: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            try {
                val response: Response<AuthResponse> = repository.recoverPassword(email)

                if (response.isSuccessful) {
                    onResult(Result.success("Email de recuperación enviado"))
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    onResult(Result.failure(Exception(errorMessage)))
                }
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }




}
