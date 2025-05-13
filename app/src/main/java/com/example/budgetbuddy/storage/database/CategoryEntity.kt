package com.example.budgetbuddy.storage.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val name: String
)
