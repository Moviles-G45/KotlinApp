package com.example.budgetbuddy.storage.database

import com.example.budgetbuddy.model.ATM

fun ATM.toEntity(): ATMEntity = ATMEntity(
    id = id,
    nombre = nombre,
    direccion = direccion,
    latitud = latitud,
    longitud = longitud,
    tipo = tipo
)

fun ATMEntity.toModel(): ATM = ATM(
    id = id,
    nombre = nombre,
    direccion = direccion,
    latitud = latitud,
    longitud = longitud,
    tipo = tipo
)