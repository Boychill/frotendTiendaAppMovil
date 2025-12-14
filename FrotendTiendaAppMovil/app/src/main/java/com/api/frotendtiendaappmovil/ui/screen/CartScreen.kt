package com.api.frotendtiendaappmovil.ui.screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.ui.components.BackButton
import com.api.frotendtiendaappmovil.ui.components.ProductDetailDialog
import com.api.frotendtiendaappmovil.ui.components.ResponsiveButton
import com.api.frotendtiendaappmovil.ui.components.ResponsiveTextField
import com.api.frotendtiendaappmovil.ui.viewmodel.CartViewModel
import com.api.frotendtiendaappmovil.util.Dimens
import com.api.frotendtiendaappmovil.util.Formatters

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val context = LocalContext.current

    // Estado para detalle de producto
    var selectedProduct by remember { mutableStateOf<ProductoDto?>(null) }

    if (selectedProduct != null) {
        ProductDetailDialog(producto = selectedProduct!!, onDismiss = { selectedProduct = null })
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) viewModel.obtenerUbicacionGps()
        else Toast.makeText(context, "Permisos requeridos", Toast.LENGTH_SHORT).show()
    }

    // Cargar direcciones guardadas al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadSavedAddresses()
    }

    LaunchedEffect(viewModel.orderSuccess) {
        if (viewModel.orderSuccess) {
            Toast.makeText(context, "¡Pedido enviado!", Toast.LENGTH_LONG).show()
            navController.navigate("catalogo") { popUpTo("catalogo") { inclusive = true } }
        }
    }

    Scaffold(
        topBar = {
            Row(modifier = Modifier.padding(top = 40.dp, start = Dimens.paddingSmall), verticalAlignment = Alignment.CenterVertically) {
                BackButton(onClick = { navController.popBackStack() })
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                Text("Confirmar Pedido", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(Dimens.paddingMedium)) {
            if (cartItems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Carrito vacío") }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cartItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedProduct = item }, // CLIC PARA DETALLE
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(modifier = Modifier.padding(Dimens.paddingMedium), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.nombre, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(Formatters.formatPrice(item.precio), color = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.removeItem(item) }) {
                                    Icon(Icons.Default.Delete, "Borrar", tint = Color.Red)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.paddingSmall))

                // --- DIRECCIONES GUARDADAS ---
                if (viewModel.savedAddresses.isNotEmpty()) {
                    Text("Mis Direcciones:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(viewModel.savedAddresses) { addr ->
                            SuggestionChip(
                                onClick = { viewModel.selectAddress(addr) },
                                label = { Text(addr.alias) },
                                icon = { Icon(Icons.Default.Place, null) }
                            )
                        }
                    }
                }

                // Campo de Dirección Manual
                ResponsiveTextField(
                    value = viewModel.deliveryAddress,
                    onValueChange = { viewModel.deliveryAddress = it },
                    label = "Dirección de Envío",
                    leadingIcon = Icons.Default.Place,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(Dimens.paddingMedium))

                // --- GPS CON BORRADO ---
                val gpsColor = if (viewModel.isGpsCaptured) Color(0xFF4CAF50) else Color.Gray

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, gpsColor, RoundedCornerShape(8.dp))
                        .padding(Dimens.paddingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (viewModel.isGpsCaptured) {
                            Text("Ubicación GPS Guardada ✅", color = gpsColor, fontWeight = FontWeight.Bold)
                            // BOTÓN BORRAR GPS
                            Text(
                                "Borrar X",
                                color = Color.Red,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { viewModel.clearGps() }.padding(top=4.dp)
                            )
                        } else {
                            Text("Sin ubicación GPS", color = Color.Gray)
                        }
                    }
                    IconButton(onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)) }) {
                        Icon(Icons.Default.LocationOn, "GPS", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.paddingLarge))

                // Totales
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL:", fontWeight = FontWeight.Bold)
                    Text(Formatters.formatPrice(viewModel.total), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(Dimens.paddingMedium))

                if (viewModel.errorMessage != null) {
                    Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                ResponsiveButton(text = "CONFIRMAR", isLoading = viewModel.isLoading, onClick = { viewModel.confirmarPedido() })
            }
        }
    }
}