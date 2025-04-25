package com.example.budgetbuddy.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
