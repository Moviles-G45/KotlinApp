package com.example.budgetbuddy.repository

import com.example.budgetbuddy.model.Transaction
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.TransactionService

class TransactionRepository {

    private val transactionService: TransactionService = ApiClient.createService(TransactionService::class.java)

    suspend fun getTransactions(authToken: String): List<Transaction> {
        return transactionService.getTransactions("Bearer $authToken")
    }
}