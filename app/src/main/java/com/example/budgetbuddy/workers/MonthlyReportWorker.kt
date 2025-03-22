package com.example.budgetbuddy.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.services.ReportService
import com.example.budgetbuddy.storage.SessionManager
import retrofit2.HttpException
import java.time.LocalDate

class MonthlyReportWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val reportService = ApiClient.createService(ReportService::class.java)
        val now = LocalDate.now()
        val month = now.monthValue
        val year = now.year

        // Recuperar el id token desde SharedPreferences encriptadas
        val sessionManager = SessionManager(applicationContext)
        val idToken = sessionManager.fetchToken() ?: return Result.failure()

        return try {
            // Enviar el token en la cabecera con el prefijo "Bearer"
            val response = reportService.sendMonthlyReport(month, year, "Bearer $idToken")
            if (response.isSuccessful) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: HttpException) {
            Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

