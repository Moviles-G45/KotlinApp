package com.example.budgetbuddy.ui.screens

import IncomeExpenseProgressBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgetbuddy.R
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.ui.components.BottomNavBar
import com.example.budgetbuddy.ui.components.BottomNavTab
import com.example.budgetbuddy.ui.theme.DarkTeal
import com.example.budgetbuddy.ui.theme.LightGreenishWhite
import com.example.budgetbuddy.ui.theme.PrimaryBlue
import com.example.budgetbuddy.ui.theme.PureWhite
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.viewmodel.CategoryViewModel
import com.example.budgetbuddy.viewmodel.TransactionViewModel
import com.example.budgetbuddy.utils.DateTimeUtils
import java.util.Calendar
import androidx.compose.ui.res.painterResource
import com.example.budgetbuddy.model.Category // Assuming this model or similar from CategoryViewModel

@Composable
fun CategoryListScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val userToken = authViewModel.getPersistedToken()

    val lastTs by transactionViewModel.lastUpdated

    LaunchedEffect(userToken) {
        userToken?.let {
            val now = Calendar.getInstance()
            now.set(Calendar.DAY_OF_MONTH, 1)
            val startDate = formatttDate(now)
            now.add(Calendar.MONTH, 1)
            val endDate = formatttDate(now)
            categoryViewModel.loadCategories()
            transactionViewModel.fetchTransactions(it, startDate, endDate)
        }
    }

    val totalExpense by transactionViewModel.totalExpense
    val totalBalance by transactionViewModel.totalBalance
    val totalIncome by transactionViewModel.totalIncome
    val categories by categoryViewModel.categories.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryBlue)
        ) {
            if (lastTs > 0L) {
                Text(
                    text = "Last update: ${DateTimeUtils.formatTimestamp(lastTs)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PureWhite,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.balance),
                                    contentDescription = "Balance Icon",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Total Balance",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTeal
                                )
                            }
                            Text(
                                text = "$${totalBalance}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFFF1FFF3)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .background(Color.White)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.expense),
                                    contentDescription = "Expense Icon",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Total Expense",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTeal
                                )
                            }
                            Text(
                                text = "-$${totalExpense}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFF39FF14)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    IncomeExpenseProgressBar(
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(bottom = 56.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        val categoryIdToUse = if (category.id != 0) category.id else 1
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color(0xFFE0F2F1))
                                .clickable {
                                    navController.navigate(
                                        Screen.CategoryTransactions.createRoute(
                                            categoryId = categoryIdToUse, // Use appropriate ID field
                                            categoryName = category.name
                                        )
                                    )
                                }
                                .padding(8.dp)
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    BottomNavBar(

                        selectedTab = BottomNavTab.HOME, // Or a dedicated tab for categories
                        onHomeClick = { navController.navigate(Screen.Home.route) },
                        onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) },
                        onMapClick = { navController.navigate(Screen.Map.route) },
                        onBudgetClick = { navController.navigate(Screen.CreateBudget.route) },
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
}

private fun formatttDate(calendar: Calendar): String {
    val y = calendar.get(Calendar.YEAR)
    val m = calendar.get(Calendar.MONTH) + 1
    val d = calendar.get(Calendar.DAY_OF_MONTH)
    return "%04d-%02d-%02d".format(y, m, d)
}