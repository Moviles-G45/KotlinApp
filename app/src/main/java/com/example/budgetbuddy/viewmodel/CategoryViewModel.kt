package com.example.budgetbuddy.viewmodel

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
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
        // Primero: tareas en IO
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val networkCategories = repository.getCategories()
                val existingCategories = categoryDao.getAll()

                if (existingCategories.isEmpty()) {
                    val categoryEntities = networkCategories.map { it.toCategoryEntity() }
                    categoryDao.insertAll(categoryEntities)
                }

                // Luego volvemos a Main para actualizar la UI
                viewModelScope.launch(Dispatchers.Main) {
                    _categories.value = networkCategories
                    _categoryCount.value = existingCategories.size
                }

            } catch (e: Exception) {
                // Si falla la red, cargamos desde la base local en IO
                val localCategories = categoryDao.getAll().map {
                    CategoryItem(it.id, it.name)
                }

                // Luego pasamos los datos al Main
                viewModelScope.launch(Dispatchers.Main) {
                    _categories.value = localCategories
                    _categoryCount.value = localCategories.size
                }
            }
        }
    }



    // Conversión de CategoryItem a CategoryEntity
    private fun CategoryItem.toCategoryEntity(): CategoryEntity {
        return CategoryEntity(id = this.id, name = this.name)
    }
}
