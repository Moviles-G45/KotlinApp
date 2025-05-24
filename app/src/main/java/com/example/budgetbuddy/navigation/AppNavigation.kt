package com.example.budgetbuddy.navigation

import android.os.Build //
import androidx.annotation.RequiresApi //
import androidx.compose.runtime.Composable //
import androidx.navigation.NavHostController //
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost //
import androidx.navigation.compose.composable //
import androidx.navigation.navArgument
import com.example.budgetbuddy.ui.screens.CreateExpenseScreen //
import com.example.budgetbuddy.ui.screens.LaunchScreen //
import com.example.budgetbuddy.ui.screens.LoginScreen //
import com.example.budgetbuddy.ui.screens.SignUpScreen //
import com.example.budgetbuddy.ui.screens.ForgotPasswordScreen //
import com.example.budgetbuddy.ui.screens.HomeScreen //
import com.example.budgetbuddy.ui.screens.SplashScreen //
import com.example.budgetbuddy.ui.screens.ATMMapScreen //
import com.example.budgetbuddy.ui.screens.CategoryListScreen //
import com.example.budgetbuddy.ui.screens.CreateBudgetScreen //
// Import the new screen
import com.example.budgetbuddy.ui.screens.CategoryTransactionsScreen
import com.example.budgetbuddy.viewmodel.AuthViewModel //
import com.example.budgetbuddy.viewmodel.BudgetViewModel //

sealed class Screen(val route: String) {
    object Splash : Screen("splash") //
    object Launch : Screen("launch") //
    object Login : Screen("login") //
    object SignUp : Screen("signup") //
    object ForgotPassword : Screen("forgot_password") //
    object Home : Screen("home") //
    object AddExpense : Screen("add_expense") //
    object Map : Screen("map") //
    object CreateBudget : Screen("create_budget") //
    object CategoryList : Screen("category_list") //

    // Added CategoryTransactions route
    object CategoryTransactions : Screen("category_transactions_screen/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: Int, categoryName: String) = "category_transactions_screen/$categoryId/$categoryName"
    }
}

@RequiresApi(Build.VERSION_CODES.O) //
@Composable
fun AppNavigation(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) { //
        composable(Screen.Splash.route) { SplashScreen(navController, authViewModel) } //
        composable(Screen.Launch.route) { LaunchScreen(navController) } //
        composable(Screen.Login.route) { LoginScreen(navController, authViewModel) } //
        composable(Screen.SignUp.route) { SignUpScreen(navController,authViewModel ) } //
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) } //
        composable(Screen.Home.route) { HomeScreen(navController, authViewModel) } //
        composable(Screen.AddExpense.route) { CreateExpenseScreen(navController, authViewModel) } //
        composable(Screen.Map.route) { ATMMapScreen(navController, authViewModel) } //
        composable(Screen.CreateBudget.route) { CreateBudgetScreen(navController,authViewModel )} //
        composable(Screen.CategoryList.route) { CategoryListScreen(navController, authViewModel) } //

        // Added composable for CategoryTransactionsScreen
        composable(
            route = Screen.CategoryTransactions.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Category"
            // Assuming CategoryTransactionsScreen is in the correct package
            CategoryTransactionsScreen(
                navController = navController,
                authViewModel = authViewModel,
                // transactionViewModel is typically provided by viewModel() delegate in the Composable
                categoryName = categoryName,
                categoryId = categoryId
            )
        }
    }
}