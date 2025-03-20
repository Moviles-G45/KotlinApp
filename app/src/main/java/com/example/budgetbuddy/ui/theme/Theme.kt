package com.example.budgetbuddy.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = DarkTeal,
    tertiary = NeonGreen,
    background = DarkGreen,
    surface = DarkTeal,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onTertiary = PureWhite
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = LightBlueAccent,
    tertiary = NeonGreen,
    background = LightGreenishWhite,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = DarkTeal,
    onTertiary = DarkTeal
)

@Composable
fun BudgetBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Disponible en Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Se usa la tipografía definida en Type.kt
        content = content
    )
}
