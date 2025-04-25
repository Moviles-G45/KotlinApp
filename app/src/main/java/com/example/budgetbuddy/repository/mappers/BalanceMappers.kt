package com.example.budgetbuddy.repository.mappers

import com.example.budgetbuddy.model.Balance
import com.example.budgetbuddy.storage.database.BalanceEntity

fun Balance.toEntity(key: String) = BalanceEntity(
    monthYear = key,
    balance, total_earnings, total_expenses, needs_spent, wants_spent, savings_spent
)
fun BalanceEntity.toModel() = Balance(
    balance, totalEarnings, totalExpenses, needsSpent, wantsSpent, savingsSpent
)
