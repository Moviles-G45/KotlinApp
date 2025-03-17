package com.example.budgetbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.budgetbuddy.navigation.Screen

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Forgot Password", fontSize = 24.sp)

        Button(onClick = { navController.navigate(Screen.Login.route) }) {
            Text(text = "Back to Login")
        }
    }
}

