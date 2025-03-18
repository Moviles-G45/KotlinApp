package com.example.budgetbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.navigation.AppNavigation
import com.example.budgetbuddy.ui.theme.BudgetBuddyTheme
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.repository.AuthRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authViewModel = AuthViewModel(AuthRepository()) // ✅ Instanciamos el ViewModel
            val navController = rememberNavController()

            BudgetBuddyTheme {
                AppNavigation(navController, authViewModel) // ✅ Pasamos authViewModel
            }
        }
    }
}
