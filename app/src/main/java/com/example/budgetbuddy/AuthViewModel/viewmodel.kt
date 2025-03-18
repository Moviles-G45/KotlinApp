package com.example.budgetbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()  // Estado inicial
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState  // UI observará este estado

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading  // ⏳ Mostrar loading
        viewModelScope.launch {
            try {
                val response = repository.login(User(email, password))
                _authState.value = AuthState.Success // ✅ Login exitoso
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed") // ❌ Error en login
            }
        }
    }
}
