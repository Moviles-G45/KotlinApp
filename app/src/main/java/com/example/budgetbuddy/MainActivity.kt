package com.example.budgetbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.navigation.AppNavigation
import com.example.budgetbuddy.ui.theme.BudgetBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetBuddyApp()
        }
    }
}

@Composable
fun BudgetBuddyApp() {
    BudgetBuddyTheme {
        val navController = rememberNavController()
        AppNavigation(navController)
    }
}
