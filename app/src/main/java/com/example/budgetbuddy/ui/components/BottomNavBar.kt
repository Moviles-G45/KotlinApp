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

@Composable
fun BottomNavBar(
    onHomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onProfileClick: () -> Unit,
    isHomeSelected: Boolean = true
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
            // Botón de Home
            IconButton(onClick = onHomeClick) {
                if (isHomeSelected) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = PrimaryBlue,
                                shape = RoundedCornerShape(22.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home Icon"
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Home Icon"
                    )
                }
            }

            // Botón para agregar expense (icono de +)
            IconButton(onClick = onAddExpenseClick) {
                Icon(
                    painter = painterResource(id = R.drawable.add_expense),
                    contentDescription = "Add Expense Icon"
                )
            }

            // Botón de Profile (cerrar sesión o perfil)
            IconButton(onClick = onProfileClick) {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile Icon"
                )
            }
        }
    }
}
