package com.example.budgetbuddy.model

import com.google.gson.annotations.SerializedName

data class ExpensesCreateRequestResponse(
    val id: Int,
    val amount: Double,
    val description: String,
    val categoryId: Int,
    val userId: Int,
    val date: String
)
