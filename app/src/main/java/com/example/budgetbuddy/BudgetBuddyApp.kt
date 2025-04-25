package com.example.budgetbuddy

import android.app.Application
import androidx.room.Room
import com.example.budgetbuddy.storage.database.AppDatabase

class BudgetBuddyApp : Application() {
    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "budgetbuddy-db"
        ).build()
    }
}
