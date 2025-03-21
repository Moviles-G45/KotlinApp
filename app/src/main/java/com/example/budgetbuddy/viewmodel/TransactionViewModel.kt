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

    private val _totalIncome = mutableStateOf(0.0)
    val totalIncome: State<Double> get() = _totalIncome

    private val _needsSpent = mutableStateOf(0.0)
    val needsSpent: State<Double> get() = _needsSpent

    private val _wantsSpent = mutableStateOf(0.0)
    val wantsSpent: State<Double> get() = _wantsSpent

    private val _savingsSpent = mutableStateOf(0.0)
    val savingsSpent: State<Double> get() = _savingsSpent

    fun fetchTransactions(
        token: String,
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            try {
                val result = repository.getTransactions(
                    authToken = token,
                    startDate = startDate,
                    endDate = endDate
                )
                _transactions.value = result

                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1
                val balance = repository.getBalance(token, year, month)

                _totalBalance.value = balance.balance
                _totalIncome.value = balance.total_earnings
                _totalExpense.value = balance.total_expenses
                _needsSpent.value = balance.needs_spent
                _wantsSpent.value = balance.wants_spent
                _savingsSpent.value = balance.savings_spent

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
