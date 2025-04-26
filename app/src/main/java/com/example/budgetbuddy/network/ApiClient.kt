package com.example.budgetbuddy.network

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.example.budgetbuddy.services.AuthService
import com.example.budgetbuddy.storage.SessionManager
import okhttp3.Interceptor
import okhttp3.Request

object ApiClient {
    private const val BASE_URL = "https://fastapi-service-185169107324.us-central1.run.app/"


    // Necesitamos acceso al contexto para obtener el token
    lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val authInterceptor = Interceptor { chain ->
        val sessionManager = SessionManager(appContext)
        val token = sessionManager.fetchToken()

        val originalRequest: Request = chain.request()
        val newRequest = originalRequest.newBuilder().apply {
            token?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()

        chain.proceed(newRequest)
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)

        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
    // 🔹 Método para obtener el servicio de autenticación
    fun createAuthService(): AuthService {
        return createService(AuthService::class.java)
    }



}
