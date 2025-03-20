package com.example.budgetbuddy.model

import com.google.gson.annotations.SerializedName

data class ExpensesCreateRequestResponse(
    val id: Int,
    val amount: Double,
    val description: String,
    @SerializedName("category_id") val categoryId: Int?,
    @SerializedName("user_id") val userId: Int?,
    val date: String
)
