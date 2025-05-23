package com.example.budgetbuddy.storage.database
import androidx.room.*

@Dao
interface ATMDao {

    @Query("SELECT * FROM atms")
    suspend fun getAll(): List<ATMEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(atms: List<ATMEntity>)

    @Query("DELETE FROM atms")
    suspend fun clearAll()
}
