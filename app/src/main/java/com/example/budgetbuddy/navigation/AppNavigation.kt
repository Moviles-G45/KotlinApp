package com.example.budgetbuddy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.budgetbuddy.ui.screens.LaunchScreen
import com.example.budgetbuddy.ui.screens.LoginScreen
import com.example.budgetbuddy.ui.screens.SignUpScreen // 🔥 Agregamos SignUp
import com.example.budgetbuddy.ui.screens.ForgotPasswordScreen // 🔥 Agregamos ForgotPassword
import com.example.budgetbuddy.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Launch : Screen("launch")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
}

@Composable
fun AppNavigation(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = Screen.Launch.route) {
        composable(Screen.Launch.route) { LaunchScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController, authViewModel) } // ✅ Pasamos authViewModel
        composable(Screen.SignUp.route) { SignUpScreen(navController) }
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }
    }
}

