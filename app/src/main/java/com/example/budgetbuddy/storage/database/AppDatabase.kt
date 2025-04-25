package com.example.budgetbuddy.storage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.budgetbuddy.storage.database.TransactionEntity
import com.example.budgetbuddy.storage.database.BalanceEntity

@Database(
    entities = [TransactionEntity::class, BalanceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun balanceDao(): BalanceDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budgetbuddy-db"
                ).build()
                INSTANCE = inst
                inst
            }
        }
    }
}
