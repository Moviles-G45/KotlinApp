package com.example.budgetbuddy.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.model.ATM
import com.example.budgetbuddy.repository.ATMRepository
import com.example.budgetbuddy.storage.database.ATMDatabase
import com.example.budgetbuddy.storage.database.toEntity
import com.example.budgetbuddy.storage.database.toModel
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
                // Obtener ubicación del usuario
                val locationDeferred = async(Dispatchers.IO) {
                    val locationHelper = LocationHelper(context)
                    locationHelper.getCurrentLocation()
                }

                val location: Location? = locationDeferred.await()

                location?.let { userLocation ->
                    val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)

                    val atms = async(Dispatchers.IO) {
                        val db = ATMDatabase.getInstance(context)
                        val atmDao = db.atmDao()

                        // Intentar primero obtener ATMs remoto
                        try {
                            val remoteATMs = atmRepository.getNearbyATMs(userLatLng.latitude, userLatLng.longitude)
                            atmDao.insertAll(remoteATMs.map { it.toEntity() })
                            remoteATMs
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // Si falla la red, obtener ATMs de Room
                            val localATMs = atmDao.getAll()
                            localATMs.map { it.toModel() }
                        }
                    }.await()

                    _uiState.value = ATMUiState(userLocation = userLatLng, atms = atms)
                } ?: run {
                    _uiState.value = ATMUiState()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
