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
import com.example.budgetbuddy.ui.components.SavingsPanel
import com.example.budgetbuddy.ui.theme.DarkGreen
import com.example.budgetbuddy.ui.theme.DarkTeal
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

    // Controlamos el estado del filtro seleccionado
    var selectedFilter by remember { mutableStateOf(FilterType.MONTHLY) }

    // Función para actualizar las transacciones según el filtro
    fun updateTransactions(filter: FilterType) {
        selectedFilter = filter
        userToken?.let { token ->
            val now = Calendar.getInstance()
            when (filter) {
                FilterType.DAILY -> {
                    // Hoy a mañana
                    val startDate = formatDate(now)
                    now.add(Calendar.DAY_OF_YEAR, 1)
                    val endDate = formatDate(now)
                    transactionViewModel.fetchTransactions(token, startDate, endDate)
                }
                FilterType.WEEKLY -> {
                    // Ajustamos al inicio de semana (lunes) hasta 7 días después
                    val dayOfWeek = now.get(Calendar.DAY_OF_WEEK) // 1=Dom, 2=Lun, ...
                    val offset = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
                    now.add(Calendar.DAY_OF_YEAR, -offset)
                    val startDate = formatDate(now)
                    now.add(Calendar.DAY_OF_YEAR, 7)
                    val endDate = formatDate(now)
                    transactionViewModel.fetchTransactions(token, startDate, endDate)
                }
                FilterType.MONTHLY -> {
                    // Inicio de mes hasta inicio de mes siguiente
                    val currentMonth = now.get(Calendar.MONTH)
                    val currentYear = now.get(Calendar.YEAR)
                    now.set(Calendar.DAY_OF_MONTH, 1)
                    val startDate = formatDate(now)
                    now.set(Calendar.MONTH, currentMonth + 1)
                    val endDate = formatDate(now)
                    transactionViewModel.fetchTransactions(token, startDate, endDate)
                }
            }
        }
    }

    // Al entrar a la pantalla, por defecto cargamos Monthly
    LaunchedEffect(userToken) {
        userToken?.let {
            updateTransactions(FilterType.MONTHLY)
        }
    }

    // Observamos los estados del ViewModel
    val transactions by transactionViewModel.transactions
    val totalExpense by transactionViewModel.totalExpense
    val totalBalance by transactionViewModel.totalBalance
    val totalIncome by transactionViewModel.totalIncome
    val needsSpent by transactionViewModel.needsSpent
    val wantsSpent by transactionViewModel.wantsSpent
    val savingsSpent by transactionViewModel.savingsSpent

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
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
        // ===== Box SUPERIOR (FONDO AZUL) =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue)
                .padding(16.dp)
        ) {
            Column {
                // Título y greeting
                Text(
                    text = "Hi, welcome back!",
                    style = MaterialTheme.typography.headlineLarge.copy(color = PureWhite)
                )
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.bodyLarge.copy(color = PureWhite)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Balance y Expense
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp) // Más espacio a los lados
                        .height(IntrinsicSize.Min),  // Para que la línea vertical se ajuste a la altura de la Row
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ===== Columna Izquierda: Total Balance =====
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.balance),
                                contentDescription = "Balance Icon",
                                modifier = Modifier.size(16.dp),  // Ajusta tamaño si lo deseas
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Total Balance",
                                style = MaterialTheme.typography.bodySmall.copy(color = DarkTeal)
                            )
                        }
                        Text(
                            text = "$${totalBalance}",
                            style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFFF1FFF3))
                        )
                    }

                    // ===== Línea divisoria vertical en el medio =====
                    Box(
                        modifier = Modifier
                            .fillMaxHeight() // Se ajusta a la altura mínima de la Row
                            .width(1.dp)
                            .background(Color.White)
                    )

                    // ===== Columna Derecha: Total Expense =====
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.expense),
                                contentDescription = "Expense Icon",
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Total Expense",
                                style = MaterialTheme.typography.bodySmall.copy(color = DarkTeal)
                            )
                        }
                        Text(
                            text = "-$${totalExpense}",
                            style = MaterialTheme.typography.headlineMedium.copy(color = NeonGreen)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    IncomeExpenseProgressBar(
                        totalIncome = totalIncome,
                        totalExpense = totalExpense
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))

                val fraction = if (totalIncome != 0.0) (totalExpense / totalIncome).toFloat() else 0f
                val percentage = (fraction * 100).toInt()

                val message = if (percentage < 80) "Looks good" else "Be careful"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$percentage% of your income. $message",
                        style = MaterialTheme.typography.bodySmall.copy(color = DarkTeal)
                    )
                }

            }
        }

        // ===== Box INFERIOR (FONDO VERDE) =====
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

                // 1) NUEVO: SavingsPanel (arriba del FilterPanel)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    SavingsPanel(
                        totalIncome = totalIncome,
                        savingsSpent = savingsSpent,
                        needsSpent = needsSpent,
                        wantsSpent = wantsSpent
                    )
                }

                Spacer(Modifier.height(16.dp))

                // 2) FilterPanel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    FilterPanel(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { filter -> updateTransactions(filter) }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // 3) Lista de transacciones
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
                    selectedTab = BottomNavTab.HOME,
                    onHomeClick = { navController.navigate(Screen.Home.route) },
                    onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) },
                    onMapClick = { navController.navigate(Screen.Map.route) },
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


// Utilidad para formatear la fecha del Calendar a "yyyy-MM-dd"
fun formatDate(calendar: Calendar): String {
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return "%04d-%02d-%02d".format(year, month, day)
}

