package com.example.budgetbuddy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.budgetbuddy.R
import com.example.budgetbuddy.navigation.Screen
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun LaunchScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🐷 Logo
        Image(
            painter = painterResource(id = R.drawable.logo), // Asegúrate de tener este recurso en res/drawable
            contentDescription = "BudgetBuddy Logo",
            modifier = Modifier.size(400.dp)

        )

        // ✍️ Subtítulo
        Text(
            text = "Spend wisely, save effortlessly.",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier
                .offset(y = -50.dp) // 🔥 Mueve el texto arriba
                .width(250.dp) // 🔥 Establece un ancho fijo más reducido

            )

        // 🔹 Botón de "Log In"
        Button(
            onClick = { navController.navigate(Screen.Login.route) },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5C88)), // 🔵 Cambia a azul

            shape = CircleShape, // 🔹 Hace que el botón sea circular
        ) {
            Text(text = "Log In", fontSize = 16.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.height(12.dp)) // Ajusta la altura según necesites

        // 🔹 Botón de "Sign Up"
        Button(
            onClick = { navController.navigate(Screen.SignUp.route) },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C8CA8)), // 🔵 Azul más claro
            shape = CircleShape, // 🔹 Hace que el botón sea circular
        ) {
            Text(text = "Sign Up", fontSize = 16.sp, color = Color.White)
        }

        // 🔹 Texto de "Forgot Password?"
        TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) {
            Text(
                text = "Forgot Password?",
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}
