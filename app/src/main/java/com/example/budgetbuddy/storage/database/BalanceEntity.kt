package com.example.budgetbuddy.storage.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "balance")
data class BalanceEntity(
    @PrimaryKey val monthYear: String,
    val balance: Double,
    val totalEarnings: Double,
    val totalExpenses: Double,
    val needsSpent: Double,
    val wantsSpent: Double,
    val savingsSpent: Double
)
