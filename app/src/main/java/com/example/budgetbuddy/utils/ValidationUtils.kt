package com.example.budgetbuddy.utils
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.Period


object ValidationUtils {

    fun isValidName(name: String): Boolean {
        return name.trim().length in 3..50
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^3\\d{9}\$")) // Ejemplo: 3XXXXXXXXX
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isValidDateOfBirth(dob: String): Boolean {
        val regex = Regex("^\\d{4}-\\d{2}-\\d{2}\$")
        if (!dob.matches(regex)) return false

        return try {
            val inputDate = LocalDate.parse(dob)
            val today = LocalDate.now()
            val yearsBetween = Period.between(inputDate, today).years
            yearsBetween >= 1
        } catch (e: Exception) {
            false
        }
    }


    fun isValidPassword(password: String): Boolean {
        return password.length in 5..20 && password.any { it.isDigit() } && password.any { it.isUpperCase() }
    }

    fun doPasswordsMatch(p1: String, p2: String): Boolean {
        return p1 == p2
    }
}
