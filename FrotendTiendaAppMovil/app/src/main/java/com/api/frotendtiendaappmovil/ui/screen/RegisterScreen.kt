package com.api.frotendtiendaappmovil.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.api.frotendtiendaappmovil.ui.components.BackButton
import com.api.frotendtiendaappmovil.ui.components.ResponsiveButton
import com.api.frotendtiendaappmovil.ui.components.ResponsiveTextField
import com.api.frotendtiendaappmovil.ui.viewmodel.RegisterViewModel
import com.api.frotendtiendaappmovil.util.Dimens

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val nameError by viewModel.nameError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

    LaunchedEffect(viewModel.registerSuccess) {
        if (viewModel.registerSuccess) {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.padding(top = 40.dp, start = Dimens.paddingSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onClick = { navController.popBackStack() })
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                Text("Crear Cuenta", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(Dimens.paddingLarge) // Margen responsivo
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResponsiveTextField(
                value = viewModel.firstName,
                onValueChange = { viewModel.firstName = it },
                label = "Nombre",
                leadingIcon = Icons.Default.Person,
                errorMessage = nameError
            )
            Spacer(modifier = Modifier.height(Dimens.paddingMedium))

            ResponsiveTextField(
                value = viewModel.lastName,
                onValueChange = { viewModel.lastName = it },
                label = "Apellido",
                leadingIcon = Icons.Default.Person,
                errorMessage = nameError
            )
            Spacer(modifier = Modifier.height(Dimens.paddingMedium))

            ResponsiveTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                errorMessage = emailError
            )
            Spacer(modifier = Modifier.height(Dimens.paddingMedium))

            ResponsiveTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = "Contraseña",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                errorMessage = passwordError
            )

            Text(
                "Mínimo 6 caracteres, 1 mayúscula y 1 número.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start).padding(start = Dimens.paddingSmall, bottom = Dimens.paddingLarge)
            )

            if (viewModel.errorMessage != null) {
                Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(Dimens.paddingMedium))
            }

            ResponsiveButton(
                text = "REGISTRARME",
                isLoading = viewModel.isLoading,
                onClick = { viewModel.onRegisterClick() }
            )
        }
    }
}