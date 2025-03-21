package com.example.budgetbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.budgetbuddy.model.TransactionRequest
import com.example.budgetbuddy.model.ExpensesCreateRequestResponse
import com.example.budgetbuddy.repository.ExpensesRepository

class TransactionCreateViewModel() : ViewModel() {

    private val repository = ExpensesRepository()

    private val _transactionResult = MutableStateFlow<Result<ExpensesCreateRequestResponse>?>(null)
    val transactionResult: StateFlow<Result<ExpensesCreateRequestResponse>?> = _transactionResult

    fun createTransaction(transaction: TransactionRequest, token: String) {
        viewModelScope.launch {
            val result = repository.addTransaction(transaction, token)

            result.fold(
                onSuccess = { response ->
                    _transactionResult.value = Result.success(response)
                },
                onFailure = { error ->
                    _transactionResult.value = Result.failure(error)
                }
            )
        }
    }
}

