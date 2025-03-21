package com.example.budgetbuddy.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgetbuddy.model.TransactionRequest
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

    LaunchedEffect(Unit) {
        categoryViewModel.fetchCategories()
    }


    val categories by categoryViewModel.categories.collectAsState()


    var date by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<Int?>(null) }
    var amount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add Expenses", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(10.dp))

        // Campo de fecha
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        var expanded by remember { mutableStateOf(false) }
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

        // Monto
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Título del gasto
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Expense Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Mensaje opcional
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Enter Message") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Botón Guardar
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
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save")
        }
    }
}
