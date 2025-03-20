package com.example.budgetbuddy.repository


import com.example.budgetbuddy.model.ExpensesCreateRequestResponse
import com.example.budgetbuddy.model.TransactionRequest
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.CreateExpensesService
import retrofit2.HttpException
import java.io.IOException

class ExpensesRepository() {

    private val expensesService: CreateExpensesService = ApiClient.createService(CreateExpensesService::class.java)

    suspend fun addTransaction(transaction: TransactionRequest, token: String): Result<ExpensesCreateRequestResponse> {
        return try {
            val response = expensesService.addTransaction(transaction, "Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                Result.failure(Exception("Error: ${response.errorBody()?.string()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.message}"))
        }
    }
}
