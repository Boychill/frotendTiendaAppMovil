package com.api.frotendtiendaappmovil.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.api.frotendtiendaappmovil.ui.components.ResponsiveButton
import com.api.frotendtiendaappmovil.ui.components.ResponsiveTextField
import com.api.frotendtiendaappmovil.ui.viewmodel.LoginViewModel
import com.api.frotendtiendaappmovil.util.Dimens
// IMPORTANTE: Asegúrate de importar tu R
import com.api.frotendtiendaappmovil.R

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.paddingLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Tienda Clan Supply MC", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(Dimens.paddingLarge))

        ResponsiveTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            leadingIcon = Icons.Default.Email,
            errorMessage = emailError
        )

        Spacer(modifier = Modifier.height(Dimens.paddingMedium))

        ResponsiveTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            leadingIcon = Icons.Default.Lock,
            visualTransformation = PasswordVisualTransformation(),
            errorMessage = passwordError
        )

        Spacer(modifier = Modifier.height(Dimens.paddingLarge))

        ResponsiveButton(
            text = "INGRESAR",
            isLoading = isLoading,
            onClick = {
                viewModel.login(
                    email = email,
                    pass = password,
                    onSuccess = {
                        navController.navigate("catalogo") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onError = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(Dimens.paddingMedium))

        TextButton(onClick = { navController.navigate("register") }) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}