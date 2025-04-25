package com.example.budgetbuddy.storage.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: Int,
    val date: String,
    val amount: Double,
    val description: String,
    val categoryName: String,
    val categoryTypeId: Int
)
