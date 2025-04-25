package com.example.budgetbuddy.repository.mappers

import com.example.budgetbuddy.model.Category
import com.example.budgetbuddy.model.CategoryType
import com.example.budgetbuddy.model.Transaction
import com.example.budgetbuddy.storage.database.TransactionEntity

fun Transaction.toEntity() = TransactionEntity(
    id, date, amount.toDouble(), description,
    category.name, category.category_type.id
)

fun TransactionEntity.toModel() = Transaction(
    id, date, amount.toString(), description,
    Category(categoryName, CategoryType(categoryTypeId))
)
