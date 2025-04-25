package com.example.budgetbuddy.services

import com.example.budgetbuddy.model.BudgetCreate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BudgetService {

    @POST("budget/") // ⚠️ Usa el endpoint exacto de tu backend FastAPI
    suspend fun setBudget(@Body budget: BudgetCreate): Response<Unit>
}
