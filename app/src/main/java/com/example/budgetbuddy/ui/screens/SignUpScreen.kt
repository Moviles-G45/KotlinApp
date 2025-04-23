package com.example.budgetbuddy.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.UserRequest
import com.example.budgetbuddy.navigation.Screen
import com.example.budgetbuddy.ui.components.PasswordTextField
import com.example.budgetbuddy.utils.NetworkStatus
import com.example.budgetbuddy.utils.SignUpFormState
import com.example.budgetbuddy.utils.isConnected
import com.example.budgetbuddy.utils.observeConnectivity

import com.example.budgetbuddy.viewmodel.AuthViewModel
import com.example.budgetbuddy.viewmodel.AuthState



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var mobileNumber by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()
    var formState by remember { mutableStateOf(SignUpFormState()) }
    val isLoading = authState is AuthState.Loading
    val context = LocalContext.current
    val networkStatus by remember(context) { observeConnectivity(context) }
        .collectAsState(initial = NetworkStatus.Unavailable)

    val hasInternet = networkStatus is NetworkStatus.Available

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        // Fondo azul oscuro en la parte superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(Color(0xFF4682B4)),
            contentAlignment = Alignment.Center // Centra el texto dentro del fondo azul
        ) {
            Text(
                text = "Create Account",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp)
                .background(
                    color = Color(0xFFD2E2F2),
                    shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                )
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState) // 🔹 Habilitar scroll
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // 📝 Campos del formulario
            CustomTextField(
                label = "Full Name",
                value = fullName,
                onValueChange = {  if (it.length <= 40) fullName = it })
            formState.fullNameError?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
            }
            CustomTextField(label = "Email", value = email, onValueChange = {  if (it.length <= 40) email = it })
            formState.emailError?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
            }
            MobileNumberTextField(
                label = "Número de Móvil",
                value = mobileNumber,
                onValueChange = {  if (it.length <= 17) mobileNumber = it })
            formState.mobileError?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
            }
            FechaTextField(
                label = "Date Of Birth",
                value = dateOfBirth,
                onValueChange = {  if (it.length <= 20) dateOfBirth = it })
            formState.dobError?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
            }
            PasswordTextField(
                label = "Password",
                password = password,
                onPasswordChange = { if (it.length <= 30) password = it },
                passwordVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible }
            )
            formState.passwordError?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
            }
            PasswordTextField(
                label = "Confirm Password",
                password = confirmPassword,
                onPasswordChange = { if (it.length <= 30) confirmPassword = it },
                passwordVisible = confirmPasswordVisible,
                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
            )
            formState.confirmPasswordError?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Texto de términos y condiciones
            Text(
                text = "By continuing, you agree to Terms of Use and Privacy Policy.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 🔹 Botón "Sign Up"
            Button(
                onClick = {
                    if (!hasInternet) {
                        // No intentes loguear si no hay internet
                        return@Button
                    }
                    formState = authViewModel.validateSignUpForm(
                        fullName, email, mobileNumber, dateOfBirth, password, confirmPassword
                    )
                    if (!formState.hasError) {
                        val user = UserRequest(fullName, email, password, dateOfBirth, mobileNumber)
                        authViewModel.signup(user)
                    }

                },

                modifier = Modifier
                    .width(200.dp)
                    .height(45.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4))
            ) {
                Text(
                    text = when {
                        !hasInternet -> "Try Again"
                        isLoading -> "Loading..."
                        else -> "Log In"
                    },
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            when (authState) {
                is AuthState.Loading -> CircularProgressIndicator()
                is AuthState.Success -> {
                    Text("Sign Up Successful!", color = MaterialTheme.colorScheme.primary)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }

                is AuthState.Error -> Text(
                    "Error: ${(authState as AuthState.Error).message}",
                    color = MaterialTheme.colorScheme.error
                )

                else -> {}
            }
            if (!hasInternet) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No internet connection. Please check your network.",
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // 🔹 Texto "Already have an account? Log In"
            Row {
                Text(text = "Already have an account?", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Log In",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}


// 🔹 Función reutilizable para TextFields normales
@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = label, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("example@example.com", color = Color.Gray) },
            shape = RoundedCornerShape(25.dp),
            maxLines = 1,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray,
                cursorColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .height(55.dp),

            )
    }
}
@Composable
fun FechaTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = label, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("YYYY-MM-DD", color = Color.Gray) },
            shape = RoundedCornerShape(25.dp),
            maxLines = 1,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray,
                cursorColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .height(55.dp),

            )
    }
}
@Composable
fun MobileNumberTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Ej: 3113447798", color = Color.Gray) },
            shape = RoundedCornerShape(25.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            maxLines = 1,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray,
                cursorColor = Color.Black
            ),

            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .height(55.dp)
        )
    }
}


