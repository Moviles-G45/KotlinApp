package com.example.budgetbuddy.utils

data class SignUpFormState(
    val fullNameError: String? = null,
    val emailError: String? = null,
    val mobileError: String? = null,
    val dobError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) {
    val hasError: Boolean
        get() = listOfNotNull(
            fullNameError,
            emailError,
            mobileError,
            dobError,
            passwordError,
            confirmPasswordError
        ).isNotEmpty()
}
