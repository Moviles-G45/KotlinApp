package com.example.budgetbuddy.utils

data class LoginFormState(
    val emailError: String? = null,
    val passwordError: String? = null
) {
    val hasError: Boolean
        get() = emailError != null || passwordError != null
}
