package com.example.budgetbuddy.storage.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface BalanceDao {
    @Query("SELECT * FROM balance WHERE monthYear = :my")
    suspend fun get(my: String): BalanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(be: BalanceEntity)
}