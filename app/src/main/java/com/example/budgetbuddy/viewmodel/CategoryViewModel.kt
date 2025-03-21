package com.example.budgetbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.CategoryItem
import com.example.budgetbuddy.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val repository = CategoryRepository()

    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = repository.getCategories()
                _categories.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
