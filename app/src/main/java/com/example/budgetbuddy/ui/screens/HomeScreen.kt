package com.example.budgetbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgetbuddy.R
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.ui.components.BottomNavBar
import com.example.budgetbuddy.ui.theme.DarkGreen
import com.example.budgetbuddy.ui.theme.LightGreenishWhite
import com.example.budgetbuddy.ui.theme.NeonGreen
import com.example.budgetbuddy.ui.theme.PrimaryBlue
import com.example.budgetbuddy.ui.theme.PureWhite
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.viewmodel.TransactionViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val userToken = authViewModel.getPersistedToken()

    LaunchedEffect(userToken) {
        userToken?.let { token ->
            transactionViewModel.fetchTransactions(token)
        }
    }

    val transactions by transactionViewModel.transactions

    val totalExpense by transactionViewModel.totalExpense

    val greeting = remember {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Hi, welcome back!",
                    style = MaterialTheme.typography.headlineLarge.copy(color = PureWhite)
                )
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.bodyLarge.copy(color = PureWhite)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.expense),
                        contentDescription = "Expense Icon",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Total Expense",
                        style = MaterialTheme.typography.bodySmall.copy(color = PureWhite)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "-$${totalExpense}",
                    style = MaterialTheme.typography.headlineMedium.copy(color = NeonGreen)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = LightGreenishWhite,
                    shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { transaction ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium),
                            colors = CardDefaults.cardColors(containerColor = LightGreenishWhite)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = transaction.description,
                                        style = MaterialTheme.typography.titleMedium.copy(color = DarkGreen)
                                    )
                                    Text(
                                        text = transaction.date,
                                        style = MaterialTheme.typography.labelSmall.copy(color = NeonGreen)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(40.dp)
                                        .background(Color(0xFF00D09E))
                                )

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = transaction.category.name,
                                        style = MaterialTheme.typography.labelMedium.copy(color = DarkGreen)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(40.dp)
                                        .background(Color(0xFF00D09E))
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    val isType1 = transaction.category.category_type.id == 1
                                    val amountStr = if (isType1) "$${transaction.amount}" else "-$${transaction.amount}"
                                    val amountColor = if (isType1) DarkGreen else NeonGreen
                                    Text(
                                        text = amountStr,
                                        style = MaterialTheme.typography.titleMedium.copy(color = amountColor)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                BottomNavBar(
                    onHomeClick = { navController.navigate(Screen.Home.route) },
                    onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) },
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
}
