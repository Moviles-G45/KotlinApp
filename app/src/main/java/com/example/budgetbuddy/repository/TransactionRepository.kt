package com.example.budgetbuddy.repository

import android.content.Context
import com.example.budgetbuddy.model.Balance
import com.example.budgetbuddy.model.Transaction
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.TransactionService
import com.example.budgetbuddy.storage.database.BalanceEntity
import com.example.budgetbuddy.storage.database.TransactionEntity
import com.example.budgetbuddy.storage.SessionManager
import com.example.budgetbuddy.storage.database.AppDatabase
import com.example.budgetbuddy.repository.mappers.toEntity
import com.example.budgetbuddy.repository.mappers.toModel

class TransactionRepository(context: Context) {

    private val transactionService: TransactionService =
        ApiClient.createService(TransactionService::class.java)

    private val dao        = AppDatabase.get(context).transactionDao()
    private val balanceDao = AppDatabase.get(context).balanceDao()

    private val session  = SessionManager(context)

    suspend fun fetchTransactionsNetworkFirst(
        authToken: String,
        startDate: String? = null,
        endDate: String? = null,
        categoryId: Int? = null
    ): List<Transaction> {
        return try {
            val remote = transactionService.getTransactions(
                authToken = "Bearer $authToken",
                startDate = startDate,
                endDate = endDate,
                categoryId = categoryId
            )

            dao.clearAll()
            dao.insertAll(remote.map { it.toEntity() })

            session.saveLastSync(System.currentTimeMillis())
            remote
        } catch (e: Exception) {
            val localTransactions = dao.getAll().map { it.toModel() }
            if (categoryId != null) {
                localTransactions.filter { it.category.id == categoryId }
            } else {
                localTransactions
            }
        }
    }

    suspend fun fetchBalanceNetworkFirst(
        authToken: String,
        year: Int,
        month: Int
    ): Balance {
        val key = "%04d-%02d".format(year, month)
        return try {
            val remote = transactionService.getBalance(
                "Bearer $authToken",
                year,
                month
            )
            balanceDao.upsert(remote.toEntity(key))
            session.saveLastSync(System.currentTimeMillis())
            remote
        } catch (e: Exception) {
            balanceDao.get(key)
                ?.toModel()
                ?: Balance(0.0,0.0,0.0,0.0,0.0,0.0)
        }
    }

    suspend fun getTransactionsFromDb(categoryId: Int? = null): List<Transaction> {
        val transactions = dao.getAll().map { it.toModel() }
        return if (categoryId != null) {
            transactions.filter { it.category.id == categoryId }
        } else {
            transactions
        }
    }

    suspend fun getBalanceFromDb(year: Int, month: Int): Balance? {
        val key = "%04d-%02d".format(year, month)
        return balanceDao.get(key)?.toModel()
    }
}
