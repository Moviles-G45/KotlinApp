package com.example.budgetbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.model.UserRequest
import com.example.budgetbuddy.model.AuthResponse

import com.example.budgetbuddy.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()  // Estado inicial
    object Loading : AuthState()
    object Successs : AuthState()
    data class Success(val response: AuthResponse) : AuthState() // ✅ Agrega esta línea
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState  // UI observará este estado

    fun signup(user: UserRequest) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.signup(user)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success(it) },
                onFailure = { AuthState.Error(it.message ?: "Error desconocido") }
            )
        }
    }


    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading  // ⏳ Mostrar loading
        viewModelScope.launch {
            try {
                val response = repository.login(User(email, password))
                _authState.value = AuthState.Successs // ✅ Login exitoso
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed") // ❌ Error en login
            }
        }
    }
}
