package com.example.budgetbuddy.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.budgetbuddy.utils.LoginFormState
import com.example.budgetbuddy.utils.SignUpFormState
import com.example.budgetbuddy.utils.ValidationUtils
import com.example.budgetbuddy.utils.isConnected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

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

) :
    ViewModel() {
    private val appContext = context.applicationContext

    private val sessionManager = SessionManager(context)


    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState // UI observará este estado

    init {
        val persistedToken = sessionManager.fetchToken()
        if (persistedToken != null) {
            if (isConnected(appContext)) {
                sendTokenToBackend(persistedToken)
            } else {
                _authState.value = AuthState.Success(AuthResponse("Inicio de sesión exitoso","1" ))
            }
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    //  SIGN UP
    fun signup(user: UserRequest) {
        _authState.value = AuthState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.signup(user)

            result.fold(
                onSuccess = {
                    // Si el signup fue exitoso, logueamos automáticamente
                    login(user.email, user.password)
                },
                onFailure = {
                        ex ->
                    val raw = ex.message.orEmpty()
                    val friendly = if ("EMAIL_EXISTS" in raw) {
                        "This email has already been used"
                    } else {
                        raw
                    }
                    _authState.value = AuthState.Error(friendly)
                }
            )
        }
    }


    // LOGIN
    fun login(email: String, password: String) {

        if (!isConnected(appContext)) {
            _authState.value = AuthState.Error("Sin conexión a internet")
            return
        }

        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Los campos no pueden estar vacíos")
            return
        }

        _authState.value = AuthState.Loading

        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        val idToken = getFirebaseIdTokenWithRetry()
                        if (idToken != null) {
                            sessionManager.saveToken(idToken)
                            kotlinx.coroutines.delay(4000)
                            sendTokenToBackend(idToken)
                        } else {
                            _authState.value = AuthState.Error("No se pudo obtener el token")
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
                val result = repository.login(UserLogin(idToken))
                _authState.value = result.fold(
                    onSuccess = {
                        AuthState.Success(it)
                    },
                    onFailure = { error ->
                        if (error.message?.contains("401") == true || error.message?.contains("Unauthorized") == true) {
                            AuthState.Unauthenticated
                        } else {
                            AuthState.Error(error.localizedMessage ?: "Login failed")
                        }
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Login failed")
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getFirebaseIdTokenWithRetry(): String? {
        val user = Firebase.auth.currentUser ?: return null
        var attempts = 0
        val maxAttempts = 5

        while (attempts < maxAttempts) {
            val token = suspendCancellableCoroutine<String?> { cont ->
                user.getIdToken(true).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(task.result?.token, null)
                    } else {
                        cont.resume(null, null)
                    }
                }
            }

            if (!token.isNullOrBlank()) return token

            attempts++
            kotlinx.coroutines.delay(300L) // ⏳ Espera 300ms antes de reintentar
        }

        return null
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


    fun setError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateSignUpForm(
        fullName: String,
        email: String,
        mobile: String,
        dob: String,
        password: String,
        confirmPassword: String
    ): SignUpFormState {
        return SignUpFormState(
            fullNameError = if (!ValidationUtils.isValidName(fullName)) "Invalid full name" else null,
            emailError = if (!ValidationUtils.isValidEmail(email)) "Invalid email address" else null,
            mobileError = if (!ValidationUtils.isValidPhone(mobile)) "Invalid mobile number" else null,
            dobError = if (!ValidationUtils.isValidDateOfBirth(dob)) "Invalid date (must be YYYY-MM-DD and at least 1 year ago)" else null,
            passwordError = if (!ValidationUtils.isValidPassword(password)) "Password must be at least 6" else null,
            confirmPasswordError = if (!ValidationUtils.doPasswordsMatch(password, confirmPassword)) "Passwords do not match" else null
        )
    }


    fun validateLoginForm(email: String, password: String): LoginFormState {
        return LoginFormState(
            emailError = if (!ValidationUtils.isValidEmail(email)) "Invalid email address" else null,
            passwordError = if (!ValidationUtils.isValidPassword(password)) "Invalid password" else null
        )
    }



}
