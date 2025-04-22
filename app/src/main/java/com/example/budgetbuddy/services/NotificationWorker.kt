package com.example.budgetbuddy.services

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.budgetbuddy.R

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification() {
        val channelId = "budget_notifications"
        val notificationId = 1
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Crear el canal de notificación (solo en Android 8+)
        val channel = NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_HIGH)
            .setName("Budget Notifications")
            .setDescription("Notificaciones de recordatorio de gastos")
            .build()
        notificationManager.createNotificationChannel(channel)

        // Construir la notificación
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.budget_buddy) // Asegúrate de tener un ícono en `res/drawable`
            .setContentTitle("¡Cuidado con tus gastos!")
            .setContentText("Recuerda revisar tu presupuesto de esta semana.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Enviar la notificación
        notificationManager.notify(notificationId, notification)
        Log.d("NotificationWorker", "Notificación enviada")
    }
}


