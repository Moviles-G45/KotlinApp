package com.example.budgetbuddy.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.ATM
import com.example.budgetbuddy.repository.ATMRepository
import com.example.budgetbuddy.utils.LocationHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ATMUiState(
    val userLocation: LatLng? = null,
    val atms: List<ATM> = emptyList()
)

class ATMViewModel : ViewModel() {

    private val atmRepository = ATMRepository()

    private val _uiState = MutableStateFlow(ATMUiState())
    val uiState: StateFlow<ATMUiState> get() = _uiState

    /** Obtiene la ubicación del usuario y la guarda en _uiState */
    fun updateUserLocation(context: Context) {
        // Launch a coroutine to call the suspend function
        viewModelScope.launch {
            try {
                val locationHelper = LocationHelper(context)
                val location = locationHelper.getCurrentLocation()
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    _uiState.value = _uiState.value.copy(userLocation = userLatLng)
                    // Una vez obtenida la ubicación, buscamos los ATMs cercanos
                    getNearbyATMs(location.latitude, location.longitude)
                } else {
                    // Handle the case when location is null, if needed
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exception appropriately
            }
        }
    }

    /** Obtiene los ATMs cercanos a la ubicación del usuario */
    private fun getNearbyATMs(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val atmList = atmRepository.getNearbyATMs(lat, lon)
                _uiState.value = _uiState.value.copy(atms = atmList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
