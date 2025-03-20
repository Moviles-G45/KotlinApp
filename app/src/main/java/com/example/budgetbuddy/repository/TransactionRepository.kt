package com.example.budgetbuddy.repository

import com.example.budgetbuddy.model.Balance
import com.example.budgetbuddy.model.Transaction
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.TransactionService

class TransactionRepository {

    private val transactionService: TransactionService =
        ApiClient.createService(TransactionService::class.java)

    suspend fun getTransactions(
        authToken: String,
        startDate: String? = null,
        endDate: String? = null
    ): List<Transaction> {
        return transactionService.getTransactions(
            authToken = "Bearer $authToken",
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getTotalSpent(authToken: String): Double {
        return transactionService.getTotalSpent("Bearer $authToken").total_spent
    }

    suspend fun getBalance(authToken: String, year: Int, month: Int): Balance {
        return transactionService.getBalance("Bearer $authToken", year, month)
    }
}
