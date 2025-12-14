package com.api.frotendtiendaappmovil.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.api.frotendtiendaappmovil.data.remote.PedidoDto
import com.api.frotendtiendaappmovil.ui.components.BackButton
import com.api.frotendtiendaappmovil.ui.components.OrderDetailDialog
import com.api.frotendtiendaappmovil.ui.viewmodel.AdminOrdersViewModel

@Composable
fun AdminOrdersScreen(
    navController: NavController,
    viewModel: AdminOrdersViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Estado para ver el detalle completo del pedido
    var selectedPedido by remember { mutableStateOf<PedidoDto?>(null) }

    // Estado para la confirmación: Guarda el pedido y el nuevo estado propuesto
    var statusConfirmation by remember { mutableStateOf<Pair<PedidoDto, String>?>(null) }

    // --- 1. DIÁLOGO DE DETALLE ---
    if (selectedPedido != null) {
        OrderDetailDialog(pedido = selectedPedido!!, onDismiss = { selectedPedido = null })
    }

    // --- 2. DIÁLOGO DE CONFIRMACIÓN ---
    if (statusConfirmation != null) {
        val (pedido, nuevoEstado) = statusConfirmation!!

        AlertDialog(
            onDismissRequest = { statusConfirmation = null },
            title = { Text("Confirmar cambio") },
            text = {
                Text("¿Estás seguro de cambiar el estado del pedido #${pedido.id.takeLast(6)} a $nuevoEstado?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateStatus(pedido, nuevoEstado)
                        statusConfirmation = null
                    }
                ) {
                    Text("Sí, cambiar")
                }
            },
            dismissButton = {
                TextButton(onClick = { statusConfirmation = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show(); viewModel.clearMessages() }
    }
    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show(); viewModel.clearMessages() }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .padding(top = 40.dp, start = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
                // Quitamos el Arrangement.SpaceBetween porque ya no hay botón a la derecha
            ) {
                BackButton(onClick = { navController.popBackStack() })
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gestión de Envíos", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Barra de Búsqueda (que agregamos en el paso anterior)
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar por ID o Estado...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = if (viewModel.searchQuery.isNotEmpty()) {
                    { IconButton(onClick = { viewModel.onSearchQueryChanged("") }) { Icon(Icons.Default.Close, null) } }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else {
                    if (viewModel.orders.isEmpty()) {
                        Text("No hay pedidos registrados", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.orders) { pedido ->
                                AdminPedidoItem(
                                    pedido = pedido,
                                    onStatusChange = { nuevoEstado ->
                                        statusConfirmation = pedido to nuevoEstado
                                    },
                                    onClick = { selectedPedido = pedido }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPedidoItem(pedido: PedidoDto, onStatusChange: (String) -> Unit, onClick: () -> Unit) {
    val estadoTexto = pedido.estado ?: "PENDIENTE"
    val statusColor = when (estadoTexto.uppercase()) {
        "ENTREGADO" -> Color(0xFF4CAF50)
        "ENVIADO" -> Color(0xFF2196F3)
        "CANCELADO" -> Color(0xFFF44336)
        else -> Color(0xFFFF9800)
    }

    Card(elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Pedido #${pedido.id.takeLast(6)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Box(modifier = Modifier.background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(estadoTexto.uppercase(), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Usamos el precio tal cual viene o el Formatter si lo prefieres importar
            Text("Total: $${pedido.total}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text("Fecha: ${pedido.fecha?.replace("T", " ")?.take(16)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text("Toca para ver detalle", style = MaterialTheme.typography.labelSmall, color = Color.LightGray, modifier = Modifier.padding(vertical = 4.dp))

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Cambiar estado:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (estadoTexto != "ENVIADO" && estadoTexto != "ENTREGADO" && estadoTexto != "CANCELADO") {
                    Button(onClick = { onStatusChange("ENVIADO") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)), contentPadding = PaddingValues(horizontal = 12.dp), modifier = Modifier.weight(1f).padding(end = 4.dp)) { Text("ENVIAR", fontSize = 12.sp) }
                }
                if (estadoTexto != "ENTREGADO" && estadoTexto != "CANCELADO") {
                    Button(onClick = { onStatusChange("ENTREGADO") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), contentPadding = PaddingValues(horizontal = 12.dp), modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) { Text("ENTREGAR", fontSize = 12.sp) }
                }
                if (estadoTexto != "CANCELADO" && estadoTexto != "ENTREGADO") {
                    Button(onClick = { onStatusChange("CANCELADO") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), contentPadding = PaddingValues(horizontal = 12.dp), modifier = Modifier.weight(1f).padding(start = 4.dp)) { Text("CANCELAR", fontSize = 12.sp) }
                }
            }
        }
    }
}