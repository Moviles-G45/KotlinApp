package com.example.budgetbuddy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.budgetbuddy.model.TransactionRequest
import com.example.budgetbuddy.utils.TransactionDiskCache
import javax.inject.Inject

class TransactionCacheViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionDiskCache = TransactionDiskCache(application.applicationContext)

    // Usamos Map<String, List<TransactionRequest>> para almacenar múltiples transacciones por token
    private val _cachedTransactions =
        MutableStateFlow<Map<String, List<TransactionRequest>>>(emptyMap())
    val cachedTransactions: StateFlow<Map<String, List<TransactionRequest>>> = _cachedTransactions

    // Agregar transacción al caché usando el token
    fun saveTransactionToCache(token: String, transaction: TransactionRequest) {
        viewModelScope.launch {
            // Obtener las transacciones actuales del usuario usando el token
            val transactions = transactionDiskCache.getTransactionsForToken(token).toMutableList()

            // Agregar la nueva transacción a la lista
            transactions.add(transaction)

            // Guardar las transacciones actualizadas en el caché
            transactionDiskCache.saveTransactionsForToken(token, transactions)

            // Ahora actualizamos el estado de las transacciones en el flujo
            _cachedTransactions.value = mapOf(token to transactions)

        }
    }


    // Recuperar las transacciones del caché usando el token
    fun getCachedTransactions(token: String) {
        viewModelScope.launch {
            // Recuperamos las transacciones del caché usando el token
            val transactions = transactionDiskCache.getTransactionsForToken(token)

            // Actualizamos el estado con las transacciones recuperadas
            _cachedTransactions.value = _cachedTransactions.value.toMutableMap().apply {
                put(token, transactions)  // Actualizamos el mapa con las transacciones del token
            }
        }
    }
}