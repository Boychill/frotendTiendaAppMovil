package com.api.frotendtiendaappmovil.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.api.frotendtiendaappmovil.data.remote.PedidoDto

@Composable
fun OrderDetailDialog(
    pedido: PedidoDto,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Cabecera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Detalle Pedido", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // Info General
                Text("ID: ${pedido.id}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("Fecha: ${pedido.fecha?.replace("T", " ")?.take(16)}", style = MaterialTheme.typography.bodyMedium)
                Text("Estado: ${pedido.estado}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(16.dp))

                // --- DIRECCIÓN Y MAPA ---
                Text("Dirección de Envío:", fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pedido.direccionEnvio ?: "No especificada",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Si hay coordenadas válidas, mostrar botón de Mapa
                if (pedido.latitud != null && pedido.longitud != null && pedido.latitud != 0.0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            // Abrir Google Maps
                            val uri = Uri.parse("geo:${pedido.latitud},${pedido.longitud}?q=${pedido.latitud},${pedido.longitud}(Entrega Pedido)")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback al navegador
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F7FA), contentColor = Color(0xFF006064)),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Map, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abrir ubicación en Mapa")
                    }
                } else {
                    // Si no hay GPS, intentamos buscar la dirección escrita
                    val direccionEscrita = pedido.direccionEnvio ?: ""
                    if (direccionEscrita.isNotBlank() && !direccionEscrita.contains("GPS")) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val uri = Uri.parse("geo:0,0?q=${Uri.encode(direccionEscrita)}")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                try { context.startActivity(intent) } catch (e: Exception) {}
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E5F5), contentColor = Color(0xFF4A148C)),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Map, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buscar dirección en Mapa")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Productos:", fontWeight = FontWeight.Bold)

                Box(modifier = Modifier.weight(1f)) {
                    if (pedido.items.isNullOrEmpty()) {
                        Text("No hay items disponibles", style = MaterialTheme.typography.bodySmall)
                    } else {
                        LazyColumn {
                            items(pedido.items) { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${item.cantidad}x ${item.nombre}", modifier = Modifier.weight(1f))
                                    Text("$${item.precio * item.cantidad}", fontWeight = FontWeight.Bold)
                                }
                                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL FINAL", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("$${pedido.total}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}