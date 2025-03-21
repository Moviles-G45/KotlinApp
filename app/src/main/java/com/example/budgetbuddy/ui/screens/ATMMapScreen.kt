package com.example.budgetbuddy.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.example.budgetbuddy.viewmodel.ATMViewModel
import com.example.budgetbuddy.model.ATM
import com.google.android.gms.maps.CameraUpdateFactory

@SuppressLint("MissingPermission")
@Composable
fun ATMMapScreen(viewModel: ATMViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val userLocation = uiState.userLocation
    val atms = uiState.atms
    var selectedATM by remember { mutableStateOf<ATM?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation ?: LatLng(0.0, 0.0), 14f)
    }

    //  Se ejecuta al abrir la pantalla, obtiene la ubicación y los ATMs cercanos
    LaunchedEffect(Unit) {
        viewModel.updateUserLocation(context)
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 14f)
            )
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            //  Ubicación del usuario en el mapa
            userLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Tu Ubicación",
                    snippet = "Estás aquí",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
            }

            //  Agregar marcadores de los ATMs
            atms.forEach { atm ->
                Marker(
                    state = MarkerState(position = LatLng(atm.latitud, atm.longitud)),
                    title = atm.nombre,
                    snippet = atm.direccion,
                    onClick = {
                        selectedATM = atm
                        true
                    }
                )
            }
        }

        //  Muestra la información del ATM seleccionado
        selectedATM?.let { atm ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Cajero: ${atm.nombre}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Dirección: ${atm.direccion}")
                    Text(text = "Tipo: ${atm.tipo}")
                }
            }
        }
    }
}