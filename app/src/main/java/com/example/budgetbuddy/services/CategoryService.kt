package com.example.budgetbuddy.services

import com.example.budgetbuddy.model.CategoryItem
import retrofit2.http.GET

interface CategoryService {
    @GET("categories")
    suspend fun getCategories(): List<CategoryItem>
}