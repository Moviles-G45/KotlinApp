package com.example.budgetbuddy.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.ui.components.BottomNavBar
import com.example.budgetbuddy.ui.components.BottomNavTab
import com.example.budgetbuddy.ui.theme.LightGreenishWhite
import com.example.budgetbuddy.ui.theme.PrimaryBlue
import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.viewmodel.BudgetViewModel
import java.time.LocalDate
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateBudgetScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: BudgetViewModel = viewModel()
) {
    var needs by remember { mutableStateOf(50f) }
    var wants by remember { mutableStateOf(30f) }
    var savings by remember { mutableStateOf(20f) }
    val total = needs.roundToInt() + wants.roundToInt() + savings.roundToInt()
    val context = LocalContext.current
    val userToken = authViewModel.getPersistedToken()

    Scaffold(
        containerColor = LightGreenishWhite,
        bottomBar = {
            BottomNavBar(
                selectedTab = BottomNavTab.ADD,
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) },
                onMapClick = { navController.navigate(Screen.Map.route) },
                onBudgetClick = { /* Already here */ },
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
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 🔷 Parte superior azul con título
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create Monthly Budget",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🟦 Tarjeta con sliders
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BudgetSlider("Needs", needs) { needs = it }
                    Spacer(modifier = Modifier.height(40.dp))

                    BudgetSlider("Wants", wants) { wants = it }
                    Spacer(modifier = Modifier.height(40.dp))

                    BudgetSlider("Savings", savings) { savings = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Total: ${total}%",  // muestra entero
                        color = if (total != 100) Color.Red else Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (total == 100) {
                                val month = LocalDate.now().monthValue
                                val year = LocalDate.now().year
                                userToken?.let { token ->
                                    viewModel.createBudget(
                                        context,
                                        needs,
                                        wants,
                                        savings,
                                        month,
                                        year,
                                        token
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Total must be 100%",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        enabled = total == 100,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(text = "Save", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontWeight = FontWeight.Medium)
            Text(text = "${value.roundToInt()}%", fontWeight = FontWeight.Medium)
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            steps = 99,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            colors = SliderDefaults.colors(
                thumbColor = PrimaryBlue,
                activeTrackColor = PrimaryBlue,
                inactiveTrackColor = Color.Gray,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )
    }
}
