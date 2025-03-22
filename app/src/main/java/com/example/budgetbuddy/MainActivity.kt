package com.example.budgetbuddy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.example.budgetbuddy.navigation.AppNavigation
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.ui.theme.BudgetBuddyTheme
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.repository.AuthRepository
import com.example.budgetbuddy.services.AuthService
import com.example.budgetbuddy.services.NotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002 // ✅ Código para solicitud de notificaciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermissions() // ✅ Ahora incluye permisos de notificación
        scheduleWeeklyNotification() // ✅ Programamos la notificación cada viernes a las 2:46 PM

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
}
