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
import kotlinx.coroutines.withContext

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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val locationHelper = LocationHelper(context)
                val location = locationHelper.getCurrentLocation()

                location?.let { userLocation ->
                    val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)
                    val remoteATMs = atmRepository.getNearbyATMs(userLatLng.latitude, userLatLng.longitude)

                    // Guardar localmente
                    val db = ATMDatabase.getInstance(context)
                    val atmDao = db.atmDao()
                    atmDao.clearAll()
                    atmDao.insertAll(remoteATMs.map { it.toEntity() })

                    // Actualizar en Main
                    withContext(Dispatchers.Main) {
                        _uiState.value = ATMUiState(userLocation = userLatLng, atms = remoteATMs)
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        _uiState.value = ATMUiState()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Si falla la red o algo más, cargar local
                val db = ATMDatabase.getInstance(context)
                val atmDao = db.atmDao()
                val localATMs = atmDao.getAll().map { it.toModel() }

                withContext(Dispatchers.Main) {
                    // Aquí puedes decidir qué hacer con la ubicación, por ejemplo null si no tienes
                    _uiState.value = ATMUiState(userLocation = null, atms = localATMs)
                }
            }
        }
    }


}
