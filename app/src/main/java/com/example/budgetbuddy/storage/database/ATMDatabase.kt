package com.example.budgetbuddy.storage.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ATMEntity::class], version = 1)
abstract class ATMDatabase : RoomDatabase() {
    abstract fun atmDao(): ATMDao

    companion object {
        @Volatile private var INSTANCE: ATMDatabase? = null

        fun getInstance(context: Context): ATMDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ATMDatabase::class.java,
                    "ATM_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
