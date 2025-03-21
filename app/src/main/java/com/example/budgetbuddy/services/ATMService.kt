package com.example.budgetbuddy.services

import com.example.budgetbuddy.model.ATM
import retrofit2.http.GET
import retrofit2.http.Query

interface ATMService {
    @GET("atms/nearby")
    suspend fun getNearbyATMs(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Double = 5.0
    ): List<ATM>
}