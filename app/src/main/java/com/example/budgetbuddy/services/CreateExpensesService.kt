package com.example.budgetbuddy.services


import com.example.budgetbuddy.model.ExpensesCreateRequestResponse
import com.example.budgetbuddy.model.TransactionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CreateExpensesService {
    @POST("/transactions")
    suspend fun addTransaction(
        @Body transaction: TransactionRequest,
        @Header("Authorization") authToken: String
    ): Response<ExpensesCreateRequestResponse>
}
