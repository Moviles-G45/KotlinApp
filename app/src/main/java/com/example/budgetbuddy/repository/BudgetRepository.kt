package com.example.budgetbuddy.repository

import com.example.budgetbuddy.model.BudgetCreate
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.BudgetService

class BudgetRepository {

    private val budgetService: BudgetService =
        ApiClient.createService(BudgetService::class.java)

    suspend fun setBudget(budget: BudgetCreate) = runCatching {
        budgetService.setBudget(budget)
    }

}
