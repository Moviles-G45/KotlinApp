package com.example.budgetbuddy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.budgetbuddy.ui.screens.LaunchScreen
import com.example.budgetbuddy.ui.screens.LoginScreen
import com.example.budgetbuddy.ui.screens.SignUpScreen // 🔥 Agregamos SignUp
import com.example.budgetbuddy.ui.screens.ForgotPasswordScreen // 🔥 Agregamos ForgotPassword

sealed class Screen(val route: String) {
    object Launch : Screen("launch")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Launch.route) {
        composable(Screen.Launch.route) { LaunchScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) } // 🔥 Nueva pantalla SignUp
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) } // 🔥 Nueva pantalla ForgotPassword
    }
}
