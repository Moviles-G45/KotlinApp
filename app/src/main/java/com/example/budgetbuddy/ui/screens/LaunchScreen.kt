package com.example.budgetbuddy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.budgetbuddy.utils.isConnected
import com.example.budgetbuddy.utils.observeConnectivity
import com.example.budgetbuddy.utils.NetworkStatus
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.StateFlow



@Composable
fun LaunchScreen(navController: NavController) {
    val context = LocalContext.current

    // 🔹 Estado inicial real: usa isConnected
    val initialConnection = isConnected(context)

    var hasInternet by remember { mutableStateOf(initialConnection) }

    // 🔹 Actualización reactiva posterior
    val networkStatus by observeConnectivity(context).collectAsState(initial = if (hasInternet) NetworkStatus.Available else NetworkStatus.Unavailable)

    var bannerVisible by remember { mutableStateOf(!initialConnection) } // 👈 Controla la visibilidad del banner

    // 🔹 Sincroniza el valor reactivo
    LaunchedEffect(networkStatus) {
        hasInternet = networkStatus is NetworkStatus.Available
        bannerVisible = !hasInternet // 👈 Muestra el banner si no hay internet

    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 🔹 Banner superior si no hay internet
        if (!hasInternet && bannerVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEAD8F1))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No internet. Please try again.",
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Close",
                    color = Color(0xFF7E4D9B),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        bannerVisible = false // 👈 Oculta el banner, pero no cambia `hasInternet`

                    }
                )
            }
        }



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "BudgetBuddy Logo",
                modifier = Modifier.size(400.dp)

            )

            Text(
                text = "Spend wisely, save effortlessly.",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier
                    .offset(y = -50.dp)
                    .width(250.dp)

            )

            // Botón de "Log In"
            Button(
                onClick = { navController.navigate(Screen.Login.route) },
                enabled = hasInternet, // desactiva si no hay internet

                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5C88)), // Cambia a azul

                shape = CircleShape, // Hace que el botón sea circular
            ) {
                Text(text = "Log In", fontSize = 16.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Botón de "Sign Up"
            Button(
                onClick = { navController.navigate(Screen.SignUp.route) },
                enabled = hasInternet, // desactiva si no hay internet
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C8CA8)),
                shape = CircleShape,
            ) {
                Text(text = "Sign Up", fontSize = 16.sp, color = Color.White)
            }

            TextButton(
                onClick = { navController.navigate(Screen.ForgotPassword.route) },
                enabled = hasInternet, // desactiva si no hay internet
            ) {
                Text(
                    text = "Forgot Password?",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

        }

    }

}
