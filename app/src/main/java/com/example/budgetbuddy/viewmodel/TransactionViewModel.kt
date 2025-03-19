package com.example.budgetbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.Transaction
import com.example.budgetbuddy.repository.TransactionRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class TransactionViewModel : ViewModel() {

    private val repository = TransactionRepository()

    private val _transactions = mutableStateOf<List<Transaction>>(emptyList())
    val transactions: State<List<Transaction>> get() = _transactions

    fun fetchTransactions(token: String) {
        viewModelScope.launch {
            try {
                val result = repository.getTransactions(token)
                _transactions.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
