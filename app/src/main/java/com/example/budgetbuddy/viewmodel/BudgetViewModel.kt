package com.example.budgetbuddy.viewmodel
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.ATM
import com.example.budgetbuddy.model.BudgetCategory
import com.example.budgetbuddy.model.BudgetCreate
import com.example.budgetbuddy.repository.ATMRepository
import com.example.budgetbuddy.repository.BudgetRepository
import com.example.budgetbuddy.utils.LocationHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BudgetViewModel : ViewModel() {
    private val repository = BudgetRepository()

    fun createBudget(needs: Float, wants: Float, savings: Float, month: Int, year: Int) {
        viewModelScope.launch {
            val body = BudgetCreate(
                month = month,
                year = year,
                budget_category_types = listOf(
                    BudgetCategory("needs", needs.toInt()),
                    BudgetCategory("wants", wants.toInt()),
                    BudgetCategory("savings", savings.toInt())
                )
            )

            val result = withContext(Dispatchers.IO) {
                repository.setBudget(body)
            }

            result.fold(
                onSuccess = {
                    Log.d("Budget", "Presupuesto guardado con éxito")
                },
                onFailure = { error: Throwable ->
                    Log.e("Budget", "Error al guardar: ${error.message}")
                }
            )

        }
    }
}
