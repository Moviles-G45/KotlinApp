package com.example.budgetbuddy.model

import com.google.gson.annotations.SerializedName

data class UserLogin(
    @SerializedName("token") val token: String
)
