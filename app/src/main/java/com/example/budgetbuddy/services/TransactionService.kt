package com.example.budgetbuddy.services

import com.example.budgetbuddy.model.Transaction
import retrofit2.http.Header
import retrofit2.http.GET

interface TransactionService {
    @GET("transactions")
    suspend fun getTransactions(
        @Header("Authorization") authToken: String
    ): List<Transaction>
}