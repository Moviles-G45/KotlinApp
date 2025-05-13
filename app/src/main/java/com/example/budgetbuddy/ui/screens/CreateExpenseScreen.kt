package com.example.budgetbuddy.ui.screens
import androidx.compose.ui.graphics.Color

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgetbuddy.model.CategoryItem
import com.example.budgetbuddy.model.TransactionRequest
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.ui.components.BottomNavBar
import com.example.budgetbuddy.ui.components.BottomNavTab
import com.example.budgetbuddy.ui.theme.LightGreenishWhite
import com.example.budgetbuddy.ui.theme.PrimaryBlue
import com.example.budgetbuddy.ui.theme.PureWhite
import com.example.budgetbuddy.utils.CategoryUsagePreferences
import com.example.budgetbuddy.utils.NetworkStatus
import com.example.budgetbuddy.utils.TransactionMemoryCache
import com.example.budgetbuddy.utils.observeConnectivity
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.viewmodel.CategoryViewModel
import com.example.budgetbuddy.viewmodel.TransactionCreateViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateExpenseScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionCreateViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val context = LocalContext.current
    val userToken = authViewModel.getPersistedToken() ?: ""




    var date by rememberSaveable { mutableStateOf("") }
    var categoryName by rememberSaveable { mutableStateOf("") }
    var categoryId by rememberSaveable { mutableStateOf<Int?>(null) }
    var amount by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val networkStatus by remember(context) { observeConnectivity(context) }
        .collectAsState(initial = NetworkStatus.Unavailable)

    val hasInternet = networkStatus is NetworkStatus.Available
    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
    }

    val categories by categoryViewModel.categories.collectAsState()
    val scrollState = rememberScrollState()
    val usageMap = remember { CategoryUsagePreferences.getCategoryUsageMap(context, userToken) }
    val sortedCategories = categories.sortedByDescending { usageMap[it.id] ?: 0 }
    val transactionResult by transactionViewModel.transactionResult.collectAsState()
    val categoryCount by categoryViewModel.categoryCount.collectAsState()

    val hasCachedTransaction = TransactionMemoryCache.hasTransaction()
    LaunchedEffect(transactionResult) {
        transactionResult?.let { result ->
            result.onSuccess {
                Toast.makeText(context, "Transaction created!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }.onFailure {
                Toast.makeText(context, "Error creating transaction: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
            transactionViewModel.clearTransactionResult()
            }
    }

    fun clearFormFields() {
        amount = ""
        title = ""
        date = ""
        categoryId = null
        categoryName = ""
    }
    Scaffold(
        containerColor = LightGreenishWhite,
        bottomBar = {
            BottomNavBar(
                selectedTab = BottomNavTab.ADD,
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryBlue)
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(16.dp)
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = PureWhite
                    )
                }

                Text(
                    text = "Add Expense",
                    style = MaterialTheme.typography.headlineLarge.copy(color = PureWhite),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(LightGreenishWhite)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // ===== Date Picker Manual =====
                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        label = { Text("Select Date") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                val calendar = Calendar.getInstance()
                                val dialog = DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val selectedCal = Calendar.getInstance()
                                        selectedCal.set(year, month, dayOfMonth)
                                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        formatter.timeZone = TimeZone.getDefault()
                                        date = formatter.format(selectedCal.time)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                )
                                dialog.datePicker.maxDate = System.currentTimeMillis()
                                dialog.show()
                            }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Date")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ===== Categoría =====
                    Box {
                        OutlinedTextField(
                            value = categoryName,
                            onValueChange = {},
                            label = { Text("Category") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Category")
                                }
                            }
                        )

                        // Observar el flujo de categorías
                        val categoriesFromDb by categoryViewModel.categories.collectAsState()

                        // Verificar si hay internet y usar las categorías correspondientes
                        val categoriesToShow = if (hasInternet) {
                            sortedCategories // Usar las categorías cargadas del backend
                        } else {
                            // Si no hay internet, usar las categorías desde el flujo
                            categoriesFromDb.map { category ->
                                CategoryItem(category.id, category.name)
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categoriesToShow.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.name) },
                                    onClick = {
                                        categoryName = item.name
                                        categoryId = item.id
                                        expanded = false
                                        CategoryUsagePreferences.incrementCategoryUsage(context, userToken, item.id)
                                    }
                                )
                            }
                        }
                    }




                    Spacer(modifier = Modifier.height(10.dp))

                    // ===== Monto =====
                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            val parsed = it.toDoubleOrNull()
                            if ((it.length <= 10 && it.all { c -> c.isDigit() || c == '.' }) &&
                                (parsed == null || parsed < 100_000_000)
                            ) {
                                amount = it
                            }
                        },
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ===== Título =====
                    OutlinedTextField(
                        value = title,
                        onValueChange = { if (it.length <= 15) title = it },
                        label = { Text("Expense Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ===== Mensaje =====
                    //Lo borre je ;)

                    // ===== Botón Guardar =====
                    Button(
                        onClick = {
                            val parsedAmount = amount.toDoubleOrNull() ?: 0.0

                            // Verificar si todos los campos están llenos
                            if (date.isNotEmpty() && categoryId != null && parsedAmount > 0 && title.isNotEmpty()) {
                                val transaction = TransactionRequest(
                                    date = date,
                                    category_id = categoryId!!,
                                    amount = parsedAmount,
                                    description = title
                                )

                                if (hasInternet) {
                                    // Guardar en el servidor
                                    transactionViewModel.createTransaction(transaction, userToken)
                                    Toast.makeText(context, "Transaction Created!", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.Home.route)
                                    clearFormFields()
                                } else {
                                    // No hay internet: verificar si ya hay algo en caché
                                    if (TransactionMemoryCache.hasTransaction()) {
                                        Toast.makeText(context, "Cannot save, there's already a transaction in cache!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val saved = transactionViewModel.saveTransactionOffline(transaction, categoryName)
                                        if (saved) {
                                            Toast.makeText(context, "Transaction saved locally!", Toast.LENGTH_SHORT).show()
                                            navController.navigate(Screen.Home.route)
                                        } else {
                                            Toast.makeText(context, "Error saving locally", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }



                            } else {
                                // Si los campos no están completos, mostrar un mensaje de error
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(
                            text = if (!hasInternet) "Save locally" else "Save",
                            color = PureWhite
                        )
                    }



                    Spacer(modifier = Modifier.height(20.dp))
                    // ===== Botón Cache =====
                    Button(
                        onClick = {
                            val cached = TransactionMemoryCache.getTransaction()
                            val cachedCategoryName = TransactionMemoryCache.getCategoryName()
                            if (cached != null) {
                                // Cargar los valores del caché en los campos del cuestionario
                                amount = cached.amount.toString()
                                title = cached.description
                                categoryId = cached.category_id
                                date = cached.date
                                categoryName = cachedCategoryName ?: ""


                                Toast.makeText(context, "Transaction restored from cache!", Toast.LENGTH_SHORT).show()

                                // Borrar el caché después de restaurar los datos
                                TransactionMemoryCache.clear()
                            } else {
                                // Si no hay transacción en el caché
                                Toast.makeText(context, "No cached transaction found.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = TransactionMemoryCache.hasTransaction() && hasInternet,  // Solo habilitar el botón si hay algo en caché
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (TransactionMemoryCache.hasTransaction()) PrimaryBlue else Color.Gray
                        )
                    ) {
                        Text(
                            text = "Restore cached transaction",
                            color = PureWhite
                        )
                    }





                }
            }
        }
    }





}

