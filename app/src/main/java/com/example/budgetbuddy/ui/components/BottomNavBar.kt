package com.example.budgetbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy.R
import com.example.budgetbuddy.ui.theme.LightBlueAccent
import com.example.budgetbuddy.ui.theme.PrimaryBlue

enum class BottomNavTab {
    HOME,
    ADD,
    PROFILE
}

@Composable
fun BottomNavBar(
    selectedTab: BottomNavTab,  // Parámetro para indicar el tab seleccionado
    onHomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                color = LightBlueAccent,
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Home
            IconButton(onClick = onHomeClick) {
                Box(
                    modifier = Modifier.size(48.dp), // Contenedor de tamaño fijo
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedTab == BottomNavTab.HOME) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    color = PrimaryBlue,
                                    shape = RoundedCornerShape(22.dp)
                                )
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Home Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Botón Add (Expense)
            IconButton(onClick = onAddExpenseClick) {
                Box(
                    modifier = Modifier.size(48.dp), // Contenedor de tamaño fijo
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedTab == BottomNavTab.ADD) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    color = PrimaryBlue,
                                    shape = RoundedCornerShape(22.dp)
                                )
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.add_expense),
                        contentDescription = "Add Expense Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Botón Profile
            IconButton(onClick = onProfileClick) {
                Box(
                    modifier = Modifier.size(48.dp), // Contenedor de tamaño fijo
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedTab == BottomNavTab.PROFILE) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    color = PrimaryBlue,
                                    shape = RoundedCornerShape(22.dp)
                                )
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
