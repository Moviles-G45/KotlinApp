package com.example.budgetbuddy.services

import com.example.budgetbuddy.model.BudgetCreate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BudgetService {

    @POST("budget/")
    suspend fun setBudget(@Body budget: BudgetCreate, @Header("Authorization") authToken: String): Response<Unit>
}
