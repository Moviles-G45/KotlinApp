package com.example.budgetbuddy.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest

class NetworkConnectionObserver(
    private val context: Context,
    private val onNetworkAvailable: () -> Unit
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            onNetworkAvailable()  // Esto se llama cuando la red vuelve
        }
    }

    // Empieza a observar la conectividad
    fun startObserving() {
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    // Deja de observar la conectividad
    fun stopObserving() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}