package com.example.budgetbuddy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.example.budgetbuddy.navigation.AppNavigation
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.ui.theme.BudgetBuddyTheme
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.repository.AuthRepository
import com.example.budgetbuddy.repository.BudgetRepository
import com.example.budgetbuddy.services.AuthService
import com.example.budgetbuddy.services.BudgetService
import com.example.budgetbuddy.services.NotificationWorker
import com.example.budgetbuddy.viewmodel.BudgetViewModel
import com.example.budgetbuddy.workers.MonthlyReportWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.example.budgetbuddy.navigation.AppNavigation
import com.example.budgetbuddy.utils.NetworkConnectionObserver
import com.example.budgetbuddy.viewmodel.TransactionCacheViewModel
import com.example.budgetbuddy.viewmodel.TransactionCreateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002


    private lateinit var networkConnectionObserver: NetworkConnectionObserver
    private lateinit var transactionCacheViewModel: TransactionCacheViewModel
    private lateinit var transactionCreateViewModel: TransactionCreateViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiClient.init(applicationContext)

        checkAndRequestPermissions() // ✅ Ahora incluye permisos de notificación
        scheduleWeeklyNotification() // ✅ Programamos la notificación cada viernes a las 2:46 PM
        scheduleMonthlyReport()

        // Inicializamos el observer para detectar la conectividad
        val authService = ApiClient.createService(AuthService::class.java)
        val authViewModel = AuthViewModel(
            AuthRepository(authService),
            context = this@MainActivity
        )
        networkConnectionObserver = NetworkConnectionObserver(
            context = this,
            onNetworkAvailable = {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Conexión a Internet detectada", Toast.LENGTH_SHORT).show()

                    val token = authViewModel.getPersistedToken()
                    if (!token.isNullOrEmpty()) {
                        launch(Dispatchers.IO) {
                            transactionCacheViewModel.cachedTransactions.collect { transactionsMap ->
                                if (transactionsMap.isNotEmpty()) {
                                    transactionsMap.forEach { (tokenKey, transactionList) ->
                                        transactionList.forEach { transaction ->
                                            transactionCreateViewModel.createTransaction(transaction, tokenKey)
                                        }
                                    }

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(applicationContext, "Transacciones enviadas correctamente", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(applicationContext, "No hay transacciones para enviar", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )



        // Iniciamos la observación de la conexión a la red
        networkConnectionObserver.startObserving()

        // Inicializamos los ViewModels
        transactionCacheViewModel = ViewModelProvider(this)[TransactionCacheViewModel::class.java]
        transactionCreateViewModel = ViewModelProvider(this)[TransactionCreateViewModel::class.java]


        setContent {
            val authService = ApiClient.createService(AuthService::class.java)
            val authViewModel = AuthViewModel(
                AuthRepository(authService),
                context = this@MainActivity
            )
            val navController = rememberNavController()

            BudgetBuddyTheme {
                AppNavigation(navController, authViewModel)
            }
        }
    }

    // 🔹 Programar notificación para los viernes a las 2:46 PM
    private fun scheduleWeeklyNotification() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY) // Viernes
            set(Calendar.HOUR_OF_DAY, 15) //(Formato 24h)
            set(Calendar.MINUTE, 36) // (Minutos)
            set(Calendar.SECOND, 0)
        }

        val now = Calendar.getInstance()
        if (calendar.before(now)) {
            // Si ya pasó el viernes de esta semana, lo programamos para la próxima semana
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        val delay = calendar.timeInMillis - now.timeInMillis


        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    // 🔹 Verificar y solicitar permisos (ubicación + notificaciones)
    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Permiso de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // 🔥 Permiso de notificaciones (solo en Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Si hay permisos pendientes, solicitarlos
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        } else {
            Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE || requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scheduleMonthlyReport() {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        // Configurar dueDate para el primer día del mes siguiente a las 00:00 horas
        dueDate.set(Calendar.DAY_OF_MONTH, 1)
        dueDate.set(Calendar.HOUR_OF_DAY, 0)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)
        dueDate.set(Calendar.MILLISECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.MONTH, 1)
        }

        val initialDelay = dueDate.timeInMillis - currentDate.timeInMillis

        // Configurar el trabajo periódico (aproximadamente cada 30 días)
        val workRequest = PeriodicWorkRequestBuilder<MonthlyReportWorker>(30L, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork("MonthlyReport", ExistingPeriodicWorkPolicy.REPLACE, workRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener la observación de la conexión para liberar recursos
        networkConnectionObserver.stopObserving()
    }

}
