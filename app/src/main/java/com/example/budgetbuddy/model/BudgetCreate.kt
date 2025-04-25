package com.example.budgetbuddy.model

data class BudgetCreate(
    val month: Int,
    val year: Int,
    val budget_category_types: List<BudgetCategory>
)