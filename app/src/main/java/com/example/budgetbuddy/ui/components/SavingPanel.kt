package com.example.budgetbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy.R
import com.example.budgetbuddy.ui.theme.DarkTeal
import com.example.budgetbuddy.ui.theme.NeonGreen
import com.example.budgetbuddy.ui.theme.PrimaryBlue

@Composable
fun SavingsPanel(
    totalIncome: Double,
    savingsSpent: Double,
    needsSpent: Double,
    wantsSpent: Double
) {
    val fraction = if (totalIncome != 0.0) (savingsSpent / totalIncome).toFloat() else 0f
    val progress = fraction.coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(PrimaryBlue)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ===== Columna IZQUIERDA (círculo de ahorro) =====
        Column(
            modifier = Modifier.weight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                // Círculo de progreso
                CircularProgressIndicator(
                    progress = {progress},
                    modifier = Modifier.fillMaxSize(),
                    color = NeonGreen,
                    strokeWidth = 6.dp
                )
                // Ícono de ahorros en el centro
                Icon(
                    painter = painterResource(id = R.drawable.savings),
                    contentDescription = "Savings Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Savings",
                style = MaterialTheme.typography.labelSmall.copy(color = DarkTeal)
            )
            Text(
                text = "${percentage}%",
                style = MaterialTheme.typography.labelSmall.copy(color = NeonGreen)
            )
        }

        // ===== Línea divisoria VERTICAL =====
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(80.dp)
                .background(Color.White)
        )

        // ==== Espacio entre la línea vertical y el contenido de la derecha ====
        Spacer(modifier = Modifier.width(18.dp))

        // ===== Columna DERECHA (Needs y Wants) =====
        Column(
            modifier = Modifier.weight(1.3f),
            horizontalAlignment = Alignment.Start
        ) {
            // --- Row para Needs ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_needs),
                    contentDescription = "Needs Icon",
                    tint = DarkTeal,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(18.dp))
                Column {
                    Text(
                        text = "Needs",
                        style = MaterialTheme.typography.labelSmall.copy(color = DarkTeal)
                    )
                    Text(
                        text = "-$${"%.2f".format(needsSpent)}",
                        style = MaterialTheme.typography.titleMedium.copy(color = NeonGreen)
                    )
                }
            }

            // ===== Línea divisoria HORIZONTAL =====
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(2.dp)
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // --- Row para Wants ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_wants),
                    contentDescription = "Wants Icon",
                    tint = DarkTeal,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(18.dp))
                Column {
                    Text(
                        text = "Wants",
                        style = MaterialTheme.typography.labelSmall.copy(color = DarkTeal)
                    )
                    Text(
                        text = "-$${"%.2f".format(wantsSpent)}",
                        style = MaterialTheme.typography.titleMedium.copy(color = NeonGreen)
                    )
                }
            }
        }
    }
}




