package com.example.budgetbuddy.utils

import com.example.budgetbuddy.model.TransactionRequest

object TransactionMemoryCache {

    private var cachedTransaction: TransactionRequest? = null
    private var cachedCategoryName: String? = null

    fun saveTransaction(transaction: TransactionRequest, categoryName: String, forceReplace: Boolean = false): Boolean {
        return if (cachedTransaction == null || forceReplace) {
            cachedTransaction = transaction
            cachedCategoryName = categoryName
            true
        } else {
            false
        }
    }

    fun getTransaction(): TransactionRequest? {
        return cachedTransaction
    }

    fun getCategoryName(): String? {
        return cachedCategoryName
    }

    fun hasTransaction(): Boolean {
        return cachedTransaction != null
    }

    fun clear() {
        cachedTransaction = null
        cachedCategoryName = null
    }
}
