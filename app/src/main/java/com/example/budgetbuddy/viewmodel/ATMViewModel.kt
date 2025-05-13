package com.example.budgetbuddy.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.ATM
import com.example.budgetbuddy.repository.ATMRepository
import com.example.budgetbuddy.utils.LocationHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    /** Obtiene la ubicación del usuario */
    fun updateUserLocationAndAtms(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                // Primero obtenemos la ubicación
                val locationDeferred = async(Dispatchers.IO) {
                    val locationHelper = LocationHelper(context)
                    locationHelper.getCurrentLocation() // Operación I/O
                }

                // Esperamos a que la ubicación esté disponible
                val location: Location? = locationDeferred.await()

                // Si la ubicación es válida, obtenemos los ATMs cercanos
                location?.let { userLocation ->
                    // Convertimos Location a LatLng
                    val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)

                    val atmsDeferred = async(Dispatchers.IO) {
                        val atmList = atmRepository.getNearbyATMs(userLatLng.latitude, userLatLng.longitude)
                        atmList // Lista de ATMs obtenida desde el repositorio
                    }

                    // Esperamos los ATMs
                    val atms = atmsDeferred.await()

                    // Actualizamos el estado con los datos obtenidos
                    _uiState.value = ATMUiState(userLocation = userLatLng, atms = atms)
                } ?: run {
                    // Si la ubicación es nula, tiramos error
                    _uiState.value = ATMUiState()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
