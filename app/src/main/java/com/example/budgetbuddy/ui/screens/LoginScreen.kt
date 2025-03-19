package com.example.budgetbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.budgetbuddy.R
import com.example.budgetbuddy.navigation.Screen
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import com.example.budgetbuddy.viewmodel.AuthState
import com.example.budgetbuddy.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState() // Observar cambios

    Box(modifier = Modifier.fillMaxSize()) {
        // 🔵 Fondo azul oscuro en la parte superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFF4682B4))
        )

        // 🔳 Sección clara con esquinas redondeadas
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 175.dp)
                .background(
                    color = Color(0xFFD2E2F2),
                    shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🏷️ Título "Welcome"
            Text(
                text = "Welcome",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.offset(y = -100.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))
            TextFieldWithLabel(label = "Username Or Email", value = email, onValueChange = { email = it })
            TextFieldWithLabel(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 🔵 Botón "Log In"
            Button(
                onClick = { authViewModel.login(email, password) },
                modifier = Modifier
                    .width(200.dp)
                    .height(45.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4))
            ) {
                Text(text = "Log In", fontSize = 16.sp)
            }

            // 🔄 Mostrar Loading, Éxito o Error
            when (authState) {
                is AuthState.Loading -> CircularProgressIndicator(color = Color.Blue)
                is AuthState.Success -> {
                    Text("Login exitoso!", color = MaterialTheme.colorScheme.primary)
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) {  // 🔥 Ir a Home en lugar de Login
                            popUpTo(Screen.SignUp.route) { inclusive = true } // 🔄 Evita regresar al SignUp
                        }                    }
                }
                is AuthState.Error -> {
                    val errorMessage = (authState as? AuthState.Error)?.message ?: "Unknown error"
                    Text(text = "Login failed: $errorMessage", color = Color.Red, fontWeight = FontWeight.Bold)
                }
                else -> {} // No hacer nada en estado inicial
            }

            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) {
                Text(text = "Forgot Password?", fontSize = 14.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { navController.navigate(Screen.SignUp.route) },
                modifier = Modifier
                    .width(200.dp)
                    .height(45.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C8CA8))
            ) {
                Text(text = "Sign Up", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 🔐 Uso de Huella Digital
            Row(
                modifier = Modifier.clickable { /* Implementar autenticación biométrica */ },
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = Icons.Filled.Fingerprint, contentDescription = "Fingerprint", tint = Color.Blue)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Use Fingerprint To Access", fontSize = 14.sp, color = Color.Blue, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🏛️ Iniciar sesión con Redes Sociales
            Row(horizontalArrangement = Arrangement.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Sign Up Link
            Row {
                Text(text = "Don't have an account?", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Sign Up", fontSize = 14.sp, color = Color.Blue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                    navController.navigate(Screen.SignUp.route)
                })
            }
        }
    }
}

// 🔹 Función reutilizable para TextFields
@Composable
fun TextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        // 📌 Etiqueta encima del campo de texto
        Text(text = label, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("example@example.com", color = Color.Gray) }, // 📌 Color del placeholder
            shape = RoundedCornerShape(25.dp), // 🔵 Hace que el campo tenga bordes redondeados
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray,
                cursorColor = Color.Black,
                errorIndicatorColor = Color.Red
            ),
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            painter = painterResource(id = R.drawable.ojo),
                            contentDescription = "Toggle Password"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(55.dp),
        )
    }
}
