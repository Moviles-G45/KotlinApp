package com.example.budgetbuddy.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.viewmodel.AuthState
import com.example.budgetbuddy.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.collectAsState().value

    LaunchedEffect(authState) {
        delay(100)
        when (authState) {
            is AuthState.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0)
                }
            }
            is AuthState.Error -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
            AuthState.Unauthenticated -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
            AuthState.Idle -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
            AuthState.Loading -> {
            }
            else -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
