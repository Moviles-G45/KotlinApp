package com.example.budgetbuddy.model

data class Transaction(
    val id: Int,
    val date: String,
    val amount: String,
    val description: String,
    val category: Category
)

data class Category(
    val name: String,
    val category_type: CategoryType
)

data class CategoryType(
    val id: Int
)

data class TotalSpent(
    val total_spent: Double
)