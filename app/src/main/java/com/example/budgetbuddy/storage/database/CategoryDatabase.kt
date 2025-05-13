package com.example.budgetbuddy.storage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.budgetbuddy.storage.database.CategoryEntity
import com.example.budgetbuddy.storage.database.CategoryDao

@Database(
    entities = [CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CategoryDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile private var INSTANCE: CategoryDatabase? = null

        fun get(context: Context): CategoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    CategoryDatabase::class.java,
                    "category-db"
                ).build()
                INSTANCE = inst
                inst
            }
        }
    }
}
