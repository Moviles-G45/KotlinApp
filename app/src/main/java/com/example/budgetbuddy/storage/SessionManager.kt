package com.example.budgetbuddy.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    companion object {
        private const val PREFS_FILENAME = "auth_prefs"
        private const val KEY_TOKEN = "firebase_token"
    }


    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILENAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun fetchToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }
}