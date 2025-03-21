package com.example.budgetbuddy.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgetbuddy.model.TransactionRequest
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.ui.components.BottomNavBar
import com.example.budgetbuddy.ui.components.BottomNavTab
import com.example.budgetbuddy.ui.components.DatePickerField
import com.example.budgetbuddy.ui.theme.LightGreenishWhite
import com.example.budgetbuddy.ui.theme.PrimaryBlue
import com.example.budgetbuddy.ui.theme.PureWhite
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.viewmodel.CategoryViewModel
import com.example.budgetbuddy.viewmodel.TransactionCreateViewModel

@Composable
fun CreateExpenseScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionCreateViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val context = LocalContext.current
    val userToken = authViewModel.getPersistedToken() ?: ""
    LaunchedEffect(Unit) { categoryViewModel.fetchCategories() }
    val categories by categoryViewModel.categories.collectAsState()
    var date by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<Int?>(null) }
    var amount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Scaffold para la barra inferior y el fondo principal
    Scaffold(
        containerColor = LightGreenishWhite, // Color de fondo general
        bottomBar = {
            BottomNavBar(
                selectedTab = BottomNavTab.ADD,
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryBlue)  // Pintamos la parte superior de azul
                .padding(paddingValues)
        ) {
            // ===== SECCIÓN SUPERIOR (FONDO AZUL + FLECHA DE REGRESO) =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(16.dp)
            ) {
                // Flecha para regresar
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

            // ===== SECCIÓN INFERIOR (FONDO VERDE CLARO) =====
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(LightGreenishWhite)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // ===== Campo de Fecha (Usando el nuevo componente) =====
                    DatePickerField(
                        selectedDate = date,
                        onDateSelected = { date = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ===== Categoría (Dropdown) =====
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
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.name) },
                                    onClick = {
                                        categoryName = item.name
                                        categoryId = item.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ===== Campo Monto =====
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ===== Campo Título =====
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Expense Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ===== Campo Mensaje Opcional =====
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Enter Message") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ===== Botón Guardar =====
                    Button(
                        onClick = {
                            val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                            if (date.isNotEmpty() && categoryId != null && parsedAmount > 0 && title.isNotEmpty()) {
                                val transaction = TransactionRequest(
                                    date = date,
                                    category_id = categoryId!!,
                                    amount = parsedAmount,
                                    description = title
                                )
                                transactionViewModel.createTransaction(transaction, userToken)
                                Toast.makeText(context, "Transaction Created!", Toast.LENGTH_SHORT).show()
                                // Regresa a la pantalla anterior
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Save", color = PureWhite)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
