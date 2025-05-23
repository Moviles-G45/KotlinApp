package com.example.budgetbuddy.storage.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "atms")
data class ATMEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val direccion: String,
    val tipo: String,
    val latitud: Double,
    val longitud: Double
)
