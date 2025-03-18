package com.example.budgetbuddy.ui.components

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerField(
    context: Context,
    label: String,
    date: String,
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth, 0, 0, 0)
            val formattedDate = dateFormat.format(calendar.time)
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = label, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)

        // 🔹 Usa Box para que detecte el clic correctamente
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(55.dp)
            .clickable { datePickerDialog.show() } // ✅ Ahora sí abre el DatePicker
        ) {
            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = false, // ✅ Sigue siendo solo lectura
                placeholder = { Text("YYYY-MM-DD", color = Color.Gray) },
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
