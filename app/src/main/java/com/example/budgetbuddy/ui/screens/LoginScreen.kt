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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import com.example.budgetbuddy.ui.components.PasswordTextField
import com.example.budgetbuddy.utils.LoginFormState
import com.example.budgetbuddy.utils.ValidationUtils
import com.example.budgetbuddy.viewmodel.AuthState
import com.example.budgetbuddy.viewmodel.AuthViewModel
import androidx.compose.runtime.saveable.rememberSaveable


@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by rememberSaveable  { mutableStateOf("") }
    var password by rememberSaveable  { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val authState by authViewModel.authState.collectAsState()
    var formState by remember { mutableStateOf(LoginFormState()) }
    val isLoading = authState is AuthState.Loading

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFF4682B4)),
            contentAlignment = Alignment.Center //Centra el texto dentro del fondo azul
        ) {
            Text(
                text = "Welcome", //Ahora se muestra bien en la parte superior
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 175.dp)
                .background(
                    color = Color(0xFFD2E2F2),
                    shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                )
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState), // Habilitar scroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            TextFieldWithLabel(label = "Email", value = email, onValueChange = { if (it.length <= 40) email = it })
            formState.emailError?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
            }
            PasswordTextField(
                label = "Password",
                password = password,
                onPasswordChange = { if (it.length <= 30) password = it  },
                passwordVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible }
            )
            formState.passwordError?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(30.dp))

            // 🔵 Botón "Log In"
            Button(
                onClick = {
                    formState = authViewModel.validateLoginForm(email, password)

                    if (!formState.hasError) {
                        authViewModel.login(email, password)
                    }
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(45.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4))
            ) {
                Text(text = if (!isLoading) "Log In" else "Loading...", fontSize = 16.sp, color = Color.White)
            }

            // Mostrar estados de autenticación
            when (authState) {
                is AuthState.Loading -> CircularProgressIndicator(color = Color.Blue)
                is AuthState.Success -> {
                    Text("Login exitoso!", color = MaterialTheme.colorScheme.primary)
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    }
                }
                is AuthState.Error -> {
                    val errorMessage = (authState as? AuthState.Error)?.message ?: "Unknown error"
                    Text(text = "Login failed: $errorMessage", color = Color.Red, fontWeight = FontWeight.Bold)
                }
                else -> {}
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

            Spacer(modifier = Modifier.height(20.dp))

            // Sign Up Link
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


// Función reutilizable para TextFields
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
        Text(text = label, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("example@example.com", color = Color.Gray) }, // Color del placeholder
            shape = RoundedCornerShape(25.dp), // Hace que el campo tenga bordes redondeados
            maxLines = 1,
            singleLine = true,
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
