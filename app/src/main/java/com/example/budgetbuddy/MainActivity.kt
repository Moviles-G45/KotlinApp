package com.example.budgetbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.navigation.AppNavigation
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.ui.theme.BudgetBuddyTheme
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.repository.AuthRepository
import com.example.budgetbuddy.services.AuthService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authService = ApiClient.createService(AuthService::class.java)
            val authViewModel = AuthViewModel(
                AuthRepository(authService),
                context = this@MainActivity
            )
            val navController = rememberNavController()

            BudgetBuddyTheme {
                AppNavigation(navController, authViewModel)
            }
        }
    }
}
