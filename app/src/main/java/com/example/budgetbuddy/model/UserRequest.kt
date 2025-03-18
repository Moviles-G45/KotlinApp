package com.example.budgetbuddy.model

data class UserRequest(
    val full_name: String,
    val email: String,
    val password: String,
    val date_of_birth: String, // Asegúrate de que el formato coincida con el backend
    val phone_number: String
)