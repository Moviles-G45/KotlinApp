package com.example.budgetbuddy.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.budgetbuddy.repository.TransactionRepository
import com.example.budgetbuddy.storage.SessionManager
import java.util.Calendar

class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val repo = TransactionRepository(applicationContext)
        val token = SessionManager(applicationContext).fetchToken()
            ?: return Result.failure()

        val now = Calendar.getInstance()
        repo.fetchTransactionsNetworkFirst(token)
        repo.fetchBalanceNetworkFirst(token, now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1)
        return Result.success()
    }
}
