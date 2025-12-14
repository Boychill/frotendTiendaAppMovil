package com.api.frotendtiendaappmovil.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.api.frotendtiendaappmovil.data.remote.AddressDto

@Composable
fun AddressSelectorAndGps(
    currentAddress: String,
    onAddressChange: (String) -> Unit,
    savedAddresses: List<AddressDto>, // Vienen del ProfileViewModel
    onSavedAddressSelected: (AddressDto) -> Unit,
    onGpsClick: () -> Unit,
    isGpsCaptured: Boolean,
    gpsCoordinates: Pair<Double, Double>,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {

        // 1. Selector de Direcciones Guardadas (Si existen)
        if (savedAddresses.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                OutlinedCard(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Usar una dirección guardada...", modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    savedAddresses.forEach { address ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(address.alias, style = MaterialTheme.typography.titleSmall)
                                    Text("${address.calle} #${address.numero}", style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = {
                                onSavedAddressSelected(address)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Text("- O escribe una nueva -", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 2. Campo de Texto Manual (Usando nuestro componente mejorado)
        ResponsiveTextField(
            value = currentAddress,
            onValueChange = onAddressChange,
            label = "Dirección de envío",
            leadingIcon = Icons.Default.LocationOn,
            errorMessage = errorMessage,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Botón de GPS
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.containsValue(true)) {
                onGpsClick()
            }
        }

        Button(
            onClick = {
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isGpsCaptured) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (isGpsCaptured) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSecondaryContainer
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            if (isGpsCaptured) {
                Text("Ubicación GPS Capturada ✅ (${gpsCoordinates.first}, ${gpsCoordinates.second})")
            } else {
                Text("Usar mi ubicación actual (GPS)")
            }
        }
    }
}