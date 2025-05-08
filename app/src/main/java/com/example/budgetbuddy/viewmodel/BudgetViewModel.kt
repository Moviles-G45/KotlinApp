package com.example.budgetbuddy.viewmodel
import android.content.Context
import android.util.Log
import android.widget.Toast
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

    fun createBudget(context: Context, needs: Float, wants: Float, savings: Float, month: Int, year: Int, token: String){
        viewModelScope.launch {
            val body = BudgetCreate(
                month = month,
                year = year,
                budget_category_types = listOf(
                    BudgetCategory("4", needs.toInt()),
                    BudgetCategory("3", wants.toInt()),
                    BudgetCategory("2", savings.toInt())
                )
            )

            val result = withContext(Dispatchers.IO) {
                repository.setBudget(token, body)
            }

            result.fold(
                onSuccess = {
                    Log.d("Budget", "Presupuesto guardado con éxito")
                    Toast.makeText(context, "Presupuesto guardado con éxito", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    // 1) Log completo de la excepción (incluye stacktrace)
                    Log.e("Budget", "Error al guardar presupuesto", error)
                    error.printStackTrace()

                    // 2) Si es HttpException, extrae el código y el body de error
                    val detailedMsg = if (error is retrofit2.HttpException) {
                        val code = error.code()
                        val body = try {
                            error.response()?.errorBody()?.string() ?: "sin body"
                        } catch (e: Exception) {
                            "error leyendo body: ${e.message}"
                        }
                        "HTTP $code: $body"
                    } else {
                        // para otras Exceptions, muestra su mensaje localizado
                        "Ya existe el presupuesto"
                    }

                    // 3) Muestra el detalle en un Toast más largo
                    Toast.makeText(context, detailedMsg, Toast.LENGTH_LONG).show()
                }
            )



        }
    }
}
