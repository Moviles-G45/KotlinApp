package com.example.budgetbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy.ui.theme.DarkGreen
import com.example.budgetbuddy.ui.theme.PrimaryBlue

enum class FilterType {
    DAILY, WEEKLY, MONTHLY
}



@Composable
fun FilterPanel(
    selectedFilter: FilterType,
    onFilterSelected: (FilterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD0E8F2), shape = RoundedCornerShape(50.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterButton(
            text = "Daily",
            isSelected = (selectedFilter == FilterType.DAILY),
            onClick = { onFilterSelected(FilterType.DAILY) }
        )
        FilterButton(
            text = "Weekly",
            isSelected = (selectedFilter == FilterType.WEEKLY),
            onClick = { onFilterSelected(FilterType.WEEKLY) }
        )
        FilterButton(
            text = "Monthly",
            isSelected = (selectedFilter == FilterType.MONTHLY),
            onClick = { onFilterSelected(FilterType.MONTHLY) }
        )
    }
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        colors = if (isSelected) {
            ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        } else {
            ButtonDefaults.buttonColors(containerColor = Color.White)
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (isSelected) Color.White else DarkGreen
            )
        )
    }
}
