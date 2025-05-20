package com.example.budgetbuddy.ui.screens

import IncomeExpenseProgressBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val userToken = authViewModel.getPersistedToken()

    // Estado del filtro
    var selectedFilter by remember { mutableStateOf(FilterType.MONTHLY) }

    // Estado de la red
    val context = LocalContext.current
    val networkStatus by observeConnectivity(context)
        .collectAsState(initial = NetworkStatus.Available)
    val hasInternet = networkStatus is NetworkStatus.Available

    // Última sincronización
    val lastTs by transactionViewModel.lastUpdated

    // Función interna para recargar transacciones
    fun updateTransactions(filter: FilterType) {
        selectedFilter = filter
        userToken?.let { token ->
            val now = Calendar.getInstance()
            when (filter) {
                FilterType.DAILY -> {
                    val startDate = formatDate(now)
                    now.add(Calendar.DAY_OF_YEAR, 1)
                    val endDate = formatDate(now)
                    transactionViewModel.fetchTransactions(token, startDate, endDate)
                }
                FilterType.WEEKLY -> {
                    val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
                    val offset = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
                    now.add(Calendar.DAY_OF_YEAR, -offset)
                    val startDate = formatDate(now)
                    now.add(Calendar.DAY_OF_YEAR, 7)
                    val endDate = formatDate(now)
                    transactionViewModel.fetchTransactions(token, startDate, endDate)
                }
                FilterType.MONTHLY -> {
                    val currentMonth = now.get(Calendar.MONTH)
                    now.set(Calendar.DAY_OF_MONTH, 1)
                    val startDate = formatDate(now)
                    now.set(Calendar.MONTH, currentMonth + 1)
                    val endDate = formatDate(now)
                    transactionViewModel.fetchTransactions(token, startDate, endDate)
                }
            }
        }
    }

    // Carga inicial
    LaunchedEffect(userToken) {
        userToken?.let { updateTransactions(FilterType.MONTHLY) }
    }

    // Estados de transacciones
    val transactions by transactionViewModel.transactions
    val totalExpense by transactionViewModel.totalExpense
    val totalBalance by transactionViewModel.totalBalance
    val totalIncome by transactionViewModel.totalIncome
    val needsSpent by transactionViewModel.needsSpent
    val wantsSpent by transactionViewModel.wantsSpent
    val savingsSpent by transactionViewModel.savingsSpent
    // Saludo según la hora
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryBlue)
        ) {
            // Banner de última actualización
            if (lastTs > 0L) {
                Text(
                    text = "Last update: ${DateTimeUtils.formatTimestamp(lastTs)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PureWhite,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Encabezado con balance y gastos
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue)
                .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Hi, welcome back!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = PureWhite
                    )
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.bodyLarge,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Total Balance
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

                        // Divisor
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .background(Color.White)
                        )

                        // Total Expense
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
                                color = NeonGreen
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

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate(Screen.CategoryList.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text("View list categories")
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    val pct = if (totalIncome != 0.0) ((totalExpense / totalIncome) * 100).toInt() else 0
                    Text(
                        text = "$pct% of your income. ${if (pct < 80) "Looks good" else "Be careful"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTeal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )
                }
            }

            // Panel inferior verde con ahorros, filtros y lista
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = LightGreenishWhite,
                        shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                    )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(Modifier.height(16.dp))

                    SavingsPanel(
                        totalIncome = totalIncome,
                        savingsSpent = savingsSpent,
                        needsSpent = needsSpent,
                        wantsSpent = wantsSpent
                    )

                    Spacer(Modifier.height(16.dp))

                    FilterPanel(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { updateTransactions(it) }
                    )

                    Spacer(Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(transactions) { tx ->
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
                                            text = tx.description,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = DarkGreen
                                        )
                                        Text(
                                            text = tx.date,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonGreen
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(40.dp)
                                            .background(Color(0xFF00D09E))
                                    )
                                    Text(
                                        text = tx.category.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = DarkGreen,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                            .align(Alignment.CenterVertically)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(40.dp)
                                            .background(Color(0xFF00D09E))
                                    )
                                    val isIncome = tx.category.category_type.id == 1
                                    Text(
                                        text = if (isIncome) "$${tx.amount}" else "-$${tx.amount}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isIncome) DarkGreen else NeonGreen,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                    }


                    BottomNavBar(
                        selectedTab = BottomNavTab.HOME,
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

            // Banner offline encima de todo
            if (!hasInternet) {
                OfflineBanner()
            }
        }
    }
}

// Util — formatea "yyyy-MM-dd"
fun formatDate(calendar: Calendar): String {
    val y = calendar.get(Calendar.YEAR)
    val m = calendar.get(Calendar.MONTH) + 1
    val d = calendar.get(Calendar.DAY_OF_MONTH)
    return "%04d-%02d-%02d".format(y, m, d)
}
