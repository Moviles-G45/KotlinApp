package com.example.budgetbuddy.repository

import com.example.budgetbuddy.model.CategoryItem
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.CategoryService

class CategoryRepository {

    private val categoryService: CategoryService =
        ApiClient.createService(CategoryService::class.java)

    suspend fun getCategories(): List<CategoryItem> {
        return categoryService.getCategories()
    }
}