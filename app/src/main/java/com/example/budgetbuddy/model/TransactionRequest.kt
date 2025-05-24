    package com.example.budgetbuddy.model

    data class TransactionRequest(
        val amount: Double,
        val description: String,
        val category_id: Int,
        val date: String
    )
