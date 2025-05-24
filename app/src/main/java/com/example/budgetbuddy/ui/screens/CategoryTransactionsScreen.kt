package com.example.budgetbuddy.ui.screens

import IncomeExpenseProgressBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgetbuddy.R
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.ui.components.BottomNavBar
import com.example.budgetbuddy.ui.components.BottomNavTab
import com.example.budgetbuddy.ui.components.FilterPanel
import com.example.budgetbuddy.ui.components.FilterType
import com.example.budgetbuddy.ui.components.OfflineBanner
import com.example.budgetbuddy.ui.components.SavingsPanel
import com.example.budgetbuddy.ui.theme.DarkGreen
import com.example.budgetbuddy.ui.theme.DarkTeal
import com.example.budgetbuddy.ui.theme.LightGreenishWhite
import com.example.budgetbuddy.ui.theme.NeonGreen
import com.example.budgetbuddy.ui.theme.PrimaryBlue
import com.example.budgetbuddy.ui.theme.PureWhite
import com.example.budgetbuddy.utils.DateTimeUtils
import com.example.budgetbuddy.utils.NetworkStatus
import com.example.budgetbuddy.utils.observeConnectivity
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.viewmodel.TransactionViewModel
import java.util.Calendar

@Composable
fun CategoryTransactionsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel = viewModel(),
    categoryName: String,
    categoryId: Int
) {
    val userToken = authViewModel.getPersistedToken()

    var selectedFilter by remember { mutableStateOf(FilterType.MONTHLY) } //

    val context = LocalContext.current //
    val networkStatus by observeConnectivity(context)
        .collectAsState(initial = NetworkStatus.Available) //
    val hasInternet = networkStatus is NetworkStatus.Available //

    val lastTs by transactionViewModel.lastUpdated //

    fun updateTransactionsForCategory(filter: FilterType) {
        selectedFilter = filter //
        userToken?.let { token ->
            val now = Calendar.getInstance() //
            val startDate: String
            val endDate: String
            when (filter) {
                FilterType.DAILY -> {
                    startDate = formattDate(now) //
                    now.add(Calendar.DAY_OF_YEAR, 1) //
                    endDate = formattDate(now) //
                }
                FilterType.WEEKLY -> {
                    val dayOfWeek = now.get(Calendar.DAY_OF_WEEK) //
                    val offset = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2 //
                    now.add(Calendar.DAY_OF_YEAR, -offset) //
                    startDate = formattDate(now) //
                    now.add(Calendar.DAY_OF_YEAR, 7) //
                    endDate = formattDate(now) //
                }
                FilterType.MONTHLY -> {
                    val currentMonth = now.get(Calendar.MONTH) //
                    now.set(Calendar.DAY_OF_MONTH, 1) //
                    startDate = formattDate(now) //
                    now.set(Calendar.MONTH, currentMonth + 1) //
                    endDate = formattDate(now) //
                }
            }
            transactionViewModel.fetchTransactions(token, startDate, endDate, categoryId) // Pass categoryId
        }
    }

    LaunchedEffect(userToken, categoryId) { //
        userToken?.let { updateTransactionsForCategory(FilterType.MONTHLY) } //
    }

    val transactions by transactionViewModel.transactions //
    val totalExpense by transactionViewModel.totalExpense //
    val totalBalance by transactionViewModel.totalBalance //
    val totalIncome by transactionViewModel.totalIncome //
    val needsSpent by transactionViewModel.needsSpent //
    val wantsSpent by transactionViewModel.wantsSpent //
    val savingsSpent by transactionViewModel.savingsSpent //

    Box(modifier = Modifier.fillMaxSize()) { //
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryBlue) //
        ) {
            if (lastTs > 0L) { //
                Text(
                    text = "Last update: ${DateTimeUtils.formatTimestamp(lastTs)}", //
                    style = MaterialTheme.typography.labelSmall, //
                    color = PureWhite, //
                    modifier = Modifier.padding(8.dp) //
                )
            }

            // Reused Top Blue Section (Header)
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue) //
                .padding(16.dp) //
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PureWhite
                            )
                        }
                        Text(
                            text = categoryName, // Display category name
                            style = MaterialTheme.typography.headlineLarge, //
                            color = PureWhite, //
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(48.dp)) // To balance the IconButton
                    }
                    Spacer(modifier = Modifier.height(16.dp)) //

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp) //
                            .height(IntrinsicSize.Min), //
                        horizontalArrangement = Arrangement.SpaceAround, //
                        verticalAlignment = Alignment.CenterVertically //
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { //
                            Row(verticalAlignment = Alignment.CenterVertically) { //
                                Icon(
                                    painter = painterResource(id = R.drawable.balance), //
                                    contentDescription = "Balance Icon", //
                                    modifier = Modifier.size(16.dp) //
                                )
                                Spacer(modifier = Modifier.width(4.dp)) //
                                Text(
                                    text = "Total Balance", //
                                    style = MaterialTheme.typography.bodySmall, //
                                    color = DarkTeal //
                                )
                            }
                            Text(
                                text = "$${totalBalance}", //
                                style = MaterialTheme.typography.headlineMedium, //
                                color = Color(0xFFF1FFF3) //
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight() //
                                .width(1.dp) //
                                .background(Color.White) //
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { //
                            Row(verticalAlignment = Alignment.CenterVertically) { //
                                Icon(
                                    painter = painterResource(id = R.drawable.expense), //
                                    contentDescription = "Expense Icon", //
                                    modifier = Modifier.size(16.dp) //
                                )
                                Spacer(modifier = Modifier.width(4.dp)) //
                                Text(
                                    text = "Total Expense", //
                                    style = MaterialTheme.typography.bodySmall, //
                                    color = DarkTeal //
                                )
                            }
                            Text(
                                text = "-$${totalExpense}", //
                                style = MaterialTheme.typography.headlineMedium, //
                                color = NeonGreen //
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp)) //
                    IncomeExpenseProgressBar(
                        totalIncome = totalIncome, //
                        totalExpense = totalExpense, //
                        modifier = Modifier
                            .fillMaxWidth() //
                            .padding(horizontal = 32.dp) //
                    )
                    Spacer(modifier = Modifier.height(16.dp)) //

                    val pct = if (totalIncome != 0.0) ((totalExpense / totalIncome) * 100).toInt() else 0 //
                    Text(
                        text = "$pct% of your income. ${if (pct < 80) "Looks good" else "Be careful"}", //
                        style = MaterialTheme.typography.bodySmall, //
                        color = DarkTeal, //
                        modifier = Modifier
                            .fillMaxWidth() //
                            .padding(horizontal = 32.dp) //
                    )
                }
            }

            // Reused Bottom Green Panel (Savings, Filters, List, Navbar)
            Box(
                modifier = Modifier
                    .fillMaxSize() //
                    .background(
                        color = LightGreenishWhite, //
                        shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp) //
                    )
            ) {
                Column(modifier = Modifier.fillMaxSize()) { //
                    Spacer(Modifier.height(16.dp)) //

                    SavingsPanel( //
                        totalIncome = totalIncome,
                        savingsSpent = savingsSpent,
                        needsSpent = needsSpent,
                        wantsSpent = wantsSpent
                    )

                    Spacer(Modifier.height(16.dp)) //

                    FilterPanel( //
                        selectedFilter = selectedFilter,
                        onFilterSelected = { filter -> updateTransactionsForCategory(filter) }
                    )

                    Spacer(Modifier.height(16.dp)) //

                    if (transactions.isEmpty()) {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transactions found for this category and filter.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkGreen
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f), //
                            contentPadding = PaddingValues(horizontal = 16.dp), //
                            verticalArrangement = Arrangement.spacedBy(8.dp) //
                        ) {
                            items(transactions) { tx -> //
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth() //
                                        .clip(MaterialTheme.shapes.medium), //
                                    colors = CardDefaults.cardColors(containerColor = LightGreenishWhite) //
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth() //
                                            .padding(12.dp), //
                                        verticalAlignment = Alignment.CenterVertically //
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) { //
                                            Text(
                                                text = tx.description, //
                                                style = MaterialTheme.typography.titleMedium, //
                                                color = DarkGreen //
                                            )
                                            Text(
                                                text = tx.date, //
                                                style = MaterialTheme.typography.labelSmall, //
                                                color = NeonGreen // Should be DarkGreen or similar for date, NeonGreen might be too bright for secondary info
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp) //
                                                .height(40.dp) //
                                                .background(Color(0xFF00D09E)) //
                                        )
                                        // Category name can be omitted here or shown differently as we are already in a category screen
                                        // For consistency with HomeScreen, it's kept.
                                        Text(
                                            text = tx.category.name, //
                                            style = MaterialTheme.typography.labelMedium, //
                                            color = DarkGreen, //
                                            modifier = Modifier
                                                .weight(1f) //
                                                .padding(horizontal = 8.dp) //
                                                .align(Alignment.CenterVertically) //
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp) //
                                                .height(40.dp) //
                                                .background(Color(0xFF00D09E)) //
                                        )
                                        val isIncome = tx.category.category_type.id == 1 //
                                        Text(
                                            text = if (isIncome) "$${tx.amount}" else "-$${tx.amount}", //
                                            style = MaterialTheme.typography.titleMedium, //
                                            color = if (isIncome) DarkGreen else NeonGreen, //
                                            modifier = Modifier.weight(0.7f) // Adjusted weight for amount
                                                .padding(start = 8.dp),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }
                        }
                    }
                    BottomNavBar( //
                        selectedTab = BottomNavTab.HOME, // Or a new tab type if needed, HOME is placeholder
                        onHomeClick = { navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        } },
                        onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) },
                        onMapClick = { navController.navigate(Screen.Map.route) },
                        onBudgetClick = { navController.navigate(Screen.CreateBudget.route) },
                        onProfileClick = {
                            authViewModel.logout() //
                            navController.navigate(Screen.Login.route) { //
                                popUpTo(Screen.Home.route) { inclusive = true } //
                            }
                        }
                    )
                }
            }

            if (!hasInternet) { //
                OfflineBanner() //
            }
        }
    }
}

// This utility function can be moved to a common utils file if not already there
// Copied from HomeScreen.kt for completeness, assuming it's accessible or defined elsewhere
private fun formattDate(calendar: Calendar): String { //
    val y = calendar.get(Calendar.YEAR) //
    val m = calendar.get(Calendar.MONTH) + 1 //
    val d = calendar.get(Calendar.DAY_OF_MONTH) //
    return "%04d-%02d-%02d".format(y, m, d) //
}