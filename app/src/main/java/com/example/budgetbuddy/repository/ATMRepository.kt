package com.example.budgetbuddy.repository

import com.example.budgetbuddy.model.ATM
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.ATMService

class ATMRepository {

    private val atmService: ATMService = ApiClient.createService(ATMService::class.java)

    suspend fun getNearbyATMs(lat: Double, lon: Double, radius: Double = 5.0): List<ATM> {
        return atmService.getNearbyATMs(lat, lon, radius)
    }
}