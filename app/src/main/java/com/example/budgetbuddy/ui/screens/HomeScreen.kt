package com.example.budgetbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.viewmodel.TransactionViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel = viewModel() // o inyectado por Hilt
) {
    // Se recupera el token persistido usando el método del AuthViewModel
    val userToken = authViewModel.getPersistedToken()

    // Llamamos a fetchTransactions cuando la pantalla se compone y el token no es nulo
    LaunchedEffect(userToken) {
        userToken?.let { token ->
            transactionViewModel.fetchTransactions(token)
        }
    }

    // Obtenemos la lista de transacciones
    val transactions by transactionViewModel.transactions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to BudgetBuddy!",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Mostrar la lista de transacciones
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = transaction.description)
                    Text(text = transaction.amount)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        ) {
            Text(text = "Log Out")
        }
    }
}
