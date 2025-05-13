package com.example.budgetbuddy.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.CategoryItem
import com.example.budgetbuddy.repository.CategoryRepository
import com.example.budgetbuddy.storage.database.CategoryDatabase
import com.example.budgetbuddy.storage.database.CategoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryDao = CategoryDatabase.get(application).categoryDao()
    private val repository = CategoryRepository()

    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories

    private val _categoryCount = MutableStateFlow<Int?>(null)
    val categoryCount: StateFlow<Int?> = _categoryCount

    // Cargar categorías
    fun loadCategories() {
        viewModelScope.launch {
            try {
                val result = repository.getCategories()
                _categories.value = result

                val existingCategories = categoryDao.getAll()
                _categoryCount.value = existingCategories.size

                if (existingCategories.isEmpty()) {
                    val categoryEntities = result.map { it.toCategoryEntity() }
                    val insertedIds = categoryDao.insertAll(categoryEntities)
                    _categoryCount.value = insertedIds.size
                }

            } catch (e: Exception) {
                val localCategories = categoryDao.getAll().map { entity ->
                    CategoryItem(entity.id, entity.name)
                }
                _categories.value = localCategories

            }
        }
    }


    // Conversión de CategoryItem a CategoryEntity
    private fun CategoryItem.toCategoryEntity(): CategoryEntity {
        return CategoryEntity(id = this.id, name = this.name)
    }
}
