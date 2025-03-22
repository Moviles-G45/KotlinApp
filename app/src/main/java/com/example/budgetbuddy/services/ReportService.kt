package com.example.budgetbuddy.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReportService {
    @GET("reports/monthly")
    suspend fun sendMonthlyReport(
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Header("Authorization") authToken: String
    ): Response<Unit>
}
