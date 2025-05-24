package com.example.budgetbuddy.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.ui.components.BottomNavBar
import com.example.budgetbuddy.ui.components.BottomNavTab
import com.example.budgetbuddy.utils.NetworkStatus
import com.example.budgetbuddy.utils.observeConnectivity
import com.example.budgetbuddy.viewmodel.AuthViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.material3.Divider

@SuppressLint("MissingPermission")
@Composable
fun ATMMapScreen(navController: NavController, authViewModel: AuthViewModel, viewModel: ATMViewModel = viewModel()) {
    val context = LocalContext.current
    val networkStatus by remember(context) { observeConnectivity(context) }.collectAsState(initial = NetworkStatus.Unavailable)
    val hasInternet = networkStatus is NetworkStatus.Available

    val uiState by viewModel.uiState.collectAsState()
    val userLocation = uiState.userLocation
    val atms = uiState.atms
    var selectedATM by remember { mutableStateOf<ATM?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation ?: LatLng(0.0, 0.0), 14f)
    }

    // Se ejecuta al abrir la pantalla, obtiene la ubicación y los ATMs cercanos
    LaunchedEffect(Unit) {
        viewModel.updateUserLocationAndAtms(context)
    }
    LaunchedEffect(hasInternet) {
        if (hasInternet) {
            // Si hay conexión, obtenemos los ATMs nuevamente
            viewModel.updateUserLocationAndAtms(context)
        } else {
            // Si no hay internet, mostramos un estado de error o vacío

        }
    }
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 14f)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (!hasInternet) {
            if (atms.isEmpty()) {
                // Si no hay datos locales, mostrar mensaje
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay conexión y no se encontraron ATMs guardados localmente.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // Mostrar lista de ATMs locales
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    items(atms) { atm ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedATM = atm },
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = atm.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(text = atm.direccion, style = MaterialTheme.typography.bodyMedium)

                            }
                        }
                        Divider()
                    }
                }
            }
        } else {
            // Mostrar el mapa normalmente
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                userLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Tu Ubicación",
                        snippet = "Estás aquí",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }

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

            selectedATM?.let { atm ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "ATM: ${atm.nombre}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Address: ${atm.direccion}")
                        Text(text = "Type: ${atm.tipo}")
                    }
                }
            }
        }

        // Bottom nav
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            BottomNavBar(
                selectedTab = BottomNavTab.MAP,
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) },
                onMapClick = { navController.navigate(Screen.Map.route) },
                onBudgetClick = { navController.navigate(Screen.CreateBudget.route) },
                onProfileClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
            }
        }
}
