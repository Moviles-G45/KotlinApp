package com.example.budgetbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.network.ApiClient
import com.example.budgetbuddy.repository.AuthRepository
import com.example.budgetbuddy.services.AuthService
import com.example.budgetbuddy.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.example.budgetbuddy.utils.NetworkStatus
import com.example.budgetbuddy.utils.isConnected
import com.example.budgetbuddy.utils.observeConnectivity

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val authService = ApiClient.retrofit.create(AuthService::class.java)
    val authRepository = AuthRepository(authService)
    val authViewModel = remember { AuthViewModel(authRepository, context) } // ✅ Instanciamos manualmente

    var email by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val topColor = Color(0xFF2F6DB5) // Azul oscuro
    val bottomColor = Color(0xFFBFDDF5) // Azul claro
    val networkStatus by remember(context) { observeConnectivity(context) }
        .collectAsState(initial = NetworkStatus.Unavailable)

    val hasInternet = networkStatus is NetworkStatus.Available


    Box(modifier = Modifier.fillMaxSize()) {
        // Sección superior con fondo azul
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(topColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Forgot Your Password?",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Sección principal con fondo claro y bordes redondeados
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp)
                .background(bottomColor, shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Texto "Type Your Email"
            Text(
                text = "Type Your Email",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de texto para el email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent, // Para ocultar borde
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón "Send Link"
            Button(
                onClick = {
                    coroutineScope.launch {
                        authViewModel.recoverPassword(email) { result ->
                            result.onSuccess {
                                snackbarMessage = "Correo de recuperación enviado"
                            }.onFailure {
                                snackbarMessage = "Error: ${it.message}"
                            }
                            showSnackbar = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F6DB5)),
                enabled = hasInternet, // ⛔ desactiva si no hay internet
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Send Link", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto de "No tienes cuenta? Sign Up"
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Don’t have an account? ", fontSize = 14.sp, color = Color.Black)
                Text(
                    text = "Sign Up",
                    fontSize = 14.sp,
                    color = Color(0xFF2F6DB5),
                    modifier = Modifier.clickable (
                        enabled = hasInternet,
                        onClick = { navController.navigate(Screen.SignUp.route) }
                    )
                        .alpha(if (hasInternet) 1f else 0.4f)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Forgot Password?",
                fontSize = 14.sp,
                color = if (hasInternet) Color.Blue else Color.Gray,
                modifier = Modifier
                    .clickable(
                        enabled = hasInternet,
                        onClick = { navController.navigate(Screen.ForgotPassword.route) }
                    )
                    .padding(0.dp)
                    .alpha(if (hasInternet) 1f else 0.4f)
            )
            Spacer(modifier = Modifier.height(30.dp))

            if (!hasInternet) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No internet connection. Please check your network.",
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

        }



    }

    if (showSnackbar) {
        Snackbar(
            action = {
                TextButton(onClick = { showSnackbar = false }) {
                    Text("OK", color = Color.White)
                }
            }
        ) {
            Text(snackbarMessage)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(navController = rememberNavController())
}
