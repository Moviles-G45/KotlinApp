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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


class TransactionCreateViewModel : ViewModel() {

    private val repository = ExpensesRepository()

    private val _transactionResult = MutableStateFlow<Result<ExpensesCreateRequestResponse>?>(null)
    val transactionResult: StateFlow<Result<ExpensesCreateRequestResponse>?> = _transactionResult

    fun createTransaction(transaction: TransactionRequest, token: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                // Ejecutamos el dispatcher IO
                val resultDeferred = async(Dispatchers.IO) {
                    repository.addTransaction(transaction, token)
                }

                // Esperamos el resultado
                val result = resultDeferred.await()

                // Ya en Main: actualizamos el estado según el resultado
                result.fold(
                    onSuccess = { response ->
                        _transactionResult.value = Result.success(response)

                        if (TransactionMemoryCache.getTransaction() == transaction) {
                            TransactionMemoryCache.clear()
                        }
                    },
                    onFailure = { error ->
                        _transactionResult.value = Result.failure(error)
                    }
                )
            } catch (e: Exception) {
                _transactionResult.value = Result.failure(e)
            }
        }
    }
            
    fun saveTransactionOffline(transaction: TransactionRequest, categoryName: String): Boolean {
        return TransactionMemoryCache.saveTransaction(transaction, categoryName)
    }

    fun clearTransactionResult() {
        _transactionResult.value = null
    }
}

