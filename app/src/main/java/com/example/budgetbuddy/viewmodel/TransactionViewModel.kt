package com.example.budgetbuddy.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.Transaction
import com.example.budgetbuddy.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel : ViewModel() {

    private val repository = TransactionRepository()

    private val _transactions = mutableStateOf<List<Transaction>>(emptyList())
    val transactions: State<List<Transaction>> get() = _transactions

    private val _totalExpense = mutableStateOf(0.0)
    val totalExpense: State<Double> get() = _totalExpense

    private val _totalBalance = mutableStateOf(0.0)
    val totalBalance: State<Double> get() = _totalBalance

    fun fetchTransactions(token: String) {
        viewModelScope.launch {
            try {
                val result = repository.getTransactions(token)
                _transactions.value = result

                val totalSpent = repository.getTotalSpent(token)
                _totalExpense.value = totalSpent

                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1
                val balance = repository.getBalance(token, year, month)
                _totalBalance.value = balance

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
