package com.example.budgetbuddy.services

import com.example.budgetbuddy.model.Balance
import com.example.budgetbuddy.model.TotalSpent
import com.example.budgetbuddy.model.Transaction
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionService {
    @GET("transactions")
    suspend fun getTransactions(
        @Header("Authorization") authToken: String,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("category") categoryId: Int? = null,
    ): List<Transaction>

    @GET("transactions/total_spent")
    suspend fun getTotalSpent(
        @Header("Authorization") authToken: String
    ): TotalSpent

    @GET("transactions/balance/{year}/{month}")
    suspend fun getBalance(
        @Header("Authorization") authToken: String,
        @Path("year") year: Int,
        @Path("month") month: Int
    ): Balance
}
