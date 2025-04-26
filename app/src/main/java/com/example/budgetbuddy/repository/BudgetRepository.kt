package com.example.budgetbuddy.repository

import com.example.budgetbuddy.model.BudgetCreate
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.BudgetService

class BudgetRepository {

    private val budgetService: BudgetService =
        ApiClient.createService(BudgetService::class.java)

    suspend fun setBudget(authToken: String, budget: BudgetCreate): Result<String> = runCatching {
        val response = budgetService.setBudget(budget, "Bearer $authToken")
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw Exception(errorBody ?: "Unknown error")
        }
        "Presupuesto creado exitosamente"
    }


}
