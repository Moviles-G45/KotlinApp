package com.example.budgetbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.budgetbuddy.model.TransactionRequest
import com.example.budgetbuddy.model.ExpensesCreateRequestResponse
import com.example.budgetbuddy.repository.ExpensesRepository
import com.example.budgetbuddy.utils.TransactionMemoryCache


class TransactionCreateViewModel : ViewModel() {

    private val repository = ExpensesRepository()

    private val _transactionResult = MutableStateFlow<Result<ExpensesCreateRequestResponse>?>(null)
    val transactionResult: StateFlow<Result<ExpensesCreateRequestResponse>?> = _transactionResult

    fun createTransaction(transaction: TransactionRequest, token: String) {
        viewModelScope.launch {
            val result = repository.addTransaction(transaction, token)

            result.fold(
                onSuccess = { response ->
                    _transactionResult.value = Result.success(response)

                    // Si se creó correctamente, limpiamos el cache por si coincide
                    if (TransactionMemoryCache.getTransaction() == transaction) {
                        TransactionMemoryCache.clear()
                    }
                },
                onFailure = { error ->
                    _transactionResult.value = Result.failure(error)
                }
            )
        }
    }

    fun saveTransactionOffline(transaction: TransactionRequest, categoryName: String): Boolean {
        return TransactionMemoryCache.saveTransaction(transaction, categoryName)
    }

    fun clearTransactionResult() {
        _transactionResult.value = null
    }
}

