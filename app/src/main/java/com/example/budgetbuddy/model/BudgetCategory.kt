package com.example.budgetbuddy.model


data class BudgetCategory(
    val category_type: String,  // o ID si tu backend lo requiere
    val percentage: Int
)