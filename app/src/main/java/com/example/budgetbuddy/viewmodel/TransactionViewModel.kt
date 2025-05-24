package com.example.budgetbuddy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.budgetbuddy.model.Transaction
import com.example.budgetbuddy.repository.TransactionRepository
import com.example.budgetbuddy.storage.SessionManager
import com.example.budgetbuddy.utils.isConnected
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val ctx = getApplication<Application>()
    private val repository = TransactionRepository(ctx)
    private val sessionManager = SessionManager(ctx)

    private val _transactions = mutableStateOf<List<Transaction>>(emptyList())
    val transactions: State<List<Transaction>> get() = _transactions

    private val _totalBalance = mutableStateOf(0.0)
    val totalBalance: State<Double> get() = _totalBalance

    private val _totalIncome = mutableStateOf(0.0)
    val totalIncome: State<Double> get() = _totalIncome

    private val _totalExpense = mutableStateOf(0.0)
    val totalExpense: State<Double> get() = _totalExpense

    private val _needsSpent = mutableStateOf(0.0)
    val needsSpent: State<Double> get() = _needsSpent

    private val _wantsSpent = mutableStateOf(0.0)
    val wantsSpent: State<Double> get() = _wantsSpent

    private val _savingsSpent = mutableStateOf(0.0)
    val savingsSpent: State<Double> get() = _savingsSpent

    private val _lastUpdated = mutableStateOf(0L)
    val lastUpdated: State<Long> get() = _lastUpdated

    fun fetchTransactions(token: String, startDate: String? = null, endDate: String? = null, categoryId: Int? = null) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        viewModelScope.launch {
            if (isConnected(ctx)) {
                val remoteTrans = repository.fetchTransactionsNetworkFirst(token, startDate, endDate, categoryId)
                _transactions.value = remoteTrans



                val bal = repository.fetchBalanceNetworkFirst(token, year, month)
                _totalBalance.value = bal.balance
                _totalIncome.value = bal.total_earnings
                _totalExpense.value = bal.total_expenses
                _needsSpent.value = bal.needs_spent
                _wantsSpent.value = bal.wants_spent
                _savingsSpent.value = bal.savings_spent

                _lastUpdated.value = sessionManager.fetchLastSync()
            } else {
                _transactions.value = repository.getTransactionsFromDb(categoryId)
                repository.getBalanceFromDb(year, month)?.let {
                    _totalBalance.value = it.balance
                    _totalIncome.value = it.total_earnings
                    _totalExpense.value = it.total_expenses
                    _needsSpent.value = it.needs_spent
                    _wantsSpent.value = it.wants_spent
                    _savingsSpent.value = it.savings_spent
                }
                _lastUpdated.value = sessionManager.fetchLastSync()
            }
        }
    }
}
