package com.api.frotendtiendaappmovil.ui.screen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.api.frotendtiendaappmovil.data.remote.PedidoDto
import com.api.frotendtiendaappmovil.ui.components.BackButton
import com.api.frotendtiendaappmovil.ui.components.OrderDetailDialog
import com.api.frotendtiendaappmovil.ui.viewmodel.ClientOrdersViewModel
import com.api.frotendtiendaappmovil.util.Dimens
import com.api.frotendtiendaappmovil.util.Formatters

@Composable
fun ClientOrdersScreen(
    navController: NavController,
    viewModel: ClientOrdersViewModel = hiltViewModel()
) {
    var selectedPedido by remember { mutableStateOf<PedidoDto?>(null) }

    if (selectedPedido != null) {
        OrderDetailDialog(pedido = selectedPedido!!, onDismiss = { selectedPedido = null })
    }

    Scaffold(
        topBar = {
            Row(modifier = Modifier.padding(top = 40.dp, start = Dimens.paddingSmall, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                BackButton(onClick = { navController.popBackStack() })
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                Text("Mis Pedidos", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // --- BARRA DE BÚSQUEDA ---
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.paddingMedium, vertical = 8.dp),
                placeholder = { Text("Buscar por ID o Estado...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = if (viewModel.searchQuery.isNotEmpty()) {
                    { IconButton(onClick = { viewModel.onSearchQueryChanged("") }) { Icon(Icons.Default.Close, null) } }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Contenido
            Box(modifier = Modifier.weight(1f)) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else if (viewModel.errorMessage != null) {
                    Text(text = viewModel.errorMessage!!, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                } else {
                    if (viewModel.filteredOrders.isEmpty()) {
                        Text(text = "No se encontraron pedidos", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(Dimens.paddingMedium),
                            verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
                        ) {
                            items(viewModel.filteredOrders) { pedido ->
                                PedidoItem(pedido = pedido, onClick = { selectedPedido = pedido })
                            }
                        }
                    }
                }
            }
        }
    }
}

// ... (PedidoItem se mantiene igual que antes) ...
@Composable
fun PedidoItem(pedido: PedidoDto, onClick: () -> Unit) {
    val estadoTexto = pedido.estado ?: "PENDIENTE"
    val totalValor = pedido.total ?: 0.0
    val fechaTexto = pedido.fecha?.replace("T", " ")?.take(16) ?: "Fecha desconocida"
    val statusColor = when (estadoTexto.uppercase()) {
        "ENTREGADO" -> Color(0xFF4CAF50)
        "ENVIADO" -> Color(0xFF2196F3)
        "CANCELADO" -> Color(0xFFF44336)
        else -> Color(0xFFFF9800)
    }

    Card(elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(modifier = Modifier.padding(Dimens.paddingMedium)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Pedido #${pedido.id.takeLast(6)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Box(modifier = Modifier.background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(text = estadoTexto.uppercase(), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(Dimens.paddingSmall))
            HorizontalDivider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(Dimens.paddingSmall))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Fecha", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(text = fechaTexto, style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(text = Formatters.formatPrice(totalValor), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}