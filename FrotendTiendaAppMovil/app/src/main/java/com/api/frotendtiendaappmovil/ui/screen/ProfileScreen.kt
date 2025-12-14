package com.api.frotendtiendaappmovil.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.api.frotendtiendaappmovil.data.remote.AddressDto
import com.api.frotendtiendaappmovil.ui.components.BackButton
import com.api.frotendtiendaappmovil.ui.components.ResponsiveButton
import com.api.frotendtiendaappmovil.ui.components.ResponsiveTextField
import com.api.frotendtiendaappmovil.ui.viewmodel.ProfileViewModel
import com.api.frotendtiendaappmovil.util.Dimens
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var addressDialogState by remember { mutableStateOf<AddressDto?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current


    LaunchedEffect(userProfile) {
        userProfile?.let {
            nombre = it.nombre ?: ""
            apellido = it.apellido ?: ""
            telefono = it.telefono ?: ""
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null && !errorMessage!!.contains("inválido")) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            Row(modifier = Modifier.padding(top = 40.dp, start = Dimens.paddingSmall), verticalAlignment = Alignment.CenterVertically) {
                BackButton(onClick = { navController.popBackStack() })
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        if (isLoading && userProfile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(Dimens.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
            ) {
                item {
                    Text("Cuenta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(userProfile?.email ?: "", color = Color.Gray)
                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.paddingSmall))
                }

                item {
                    Text("Datos Personales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(Dimens.paddingSmall))
                    ResponsiveTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre")
                    Spacer(modifier = Modifier.height(Dimens.paddingSmall))
                    ResponsiveTextField(value = apellido, onValueChange = { apellido = it }, label = "Apellido")
                    Spacer(modifier = Modifier.height(Dimens.paddingSmall))
                    ResponsiveTextField(value = telefono, onValueChange = { telefono = it }, label = "Teléfono", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    Spacer(modifier = Modifier.height(Dimens.paddingMedium))
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(Dimens.paddingSmall))
                    }
                    ResponsiveButton(text = "GUARDAR DATOS", onClick = { viewModel.updateUserData(nombre, apellido, telefono) }, isLoading = isLoading)
                }

                item {
                    Spacer(modifier = Modifier.height(Dimens.paddingMedium))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Mis Direcciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        IconButton(onClick = {
                            addressDialogState = AddressDto(id=null, alias="", calle="", numero="", latitud=0.0, longitud=0.0)
                            showDialog = true
                        }) { Icon(Icons.Default.Add, "Nueva") }
                    }
                }

                items(userProfile?.addresses ?: emptyList()) { address ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(Dimens.paddingMedium), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(address.alias, fontWeight = FontWeight.Bold)
                                if (address.latitud != 0.0) Text("📍 Ubicación GPS", fontSize = 12.sp, color = Color(0xFF4CAF50))
                                else Text("${address.calle} #${address.numero}")
                            }
                            IconButton(onClick = { addressDialogState = address; showDialog = true }) { Icon(Icons.Default.Edit, "Editar", tint = Color.Blue) }
                        }
                    }
                }
            }
        }
    }

    if (showDialog && addressDialogState != null) {
        AddAddressDialog(
            initialAddress = addressDialogState!!,
            onDismiss = { showDialog = false },
            onSave = { alias, calle, numero, lat, lng ->
                viewModel.saveAddress(alias, calle, numero, lat, lng, addressDialogState?.id)
                showDialog = false
            },
            onDelete = {
                addressDialogState?.id?.let { viewModel.deleteAddress(it) }
                showDialog = false
            }
        )
    }
}

@SuppressLint("MissingPermission")
@Composable
fun AddAddressDialog(
    initialAddress: AddressDto,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Double, Double) -> Unit,
    onDelete: () -> Unit
) {
    var alias by remember { mutableStateOf(initialAddress.alias) }
    var calle by remember { mutableStateOf(initialAddress.calle) }
    var numero by remember { mutableStateOf(initialAddress.numero) }
    var latitud by remember { mutableDoubleStateOf(initialAddress.latitud) }
    var longitud by remember { mutableDoubleStateOf(initialAddress.longitud) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.values.all { it }) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc -> if (loc != null) { latitud = loc.latitude; longitud = loc.longitude } }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialAddress.id == null) "Nueva Dirección" else "Editar Dirección") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = alias, onValueChange = { alias = it }, label = { Text("Alias (Ej: Casa)") })
                OutlinedTextField(value = calle, onValueChange = { calle = it }, label = { Text("Calle") })
                OutlinedTextField(value = numero, onValueChange = { numero = it }, label = { Text("Número") })

                Button(
                    onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Usar mi GPS actual")
                }

                if (latitud != 0.0) Text("GPS Capturado ✅", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (initialAddress.id != null) {
                    TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) { Text("Eliminar") }
                }
                Button(onClick = { onSave(alias, calle, numero, latitud, longitud) }, enabled = alias.isNotBlank()) { Text("Guardar") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}