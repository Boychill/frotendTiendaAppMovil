package com.api.frotendtiendaappmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.util.Formatters

@Composable
fun ProductDetailDialog(
    producto: ProductoDto,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Box(modifier = Modifier.height(250.dp).fillMaxWidth()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(producto.imagenUrl).crossfade(true).build(),
                        contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().background(Color.LightGray)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50)).size(32.dp)) {
                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = producto.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    // PRECIO FORMATEADO
                    Text(
                        text = Formatters.formatPrice(producto.precio),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    if (!producto.categorias.isNullOrEmpty()) {
                        Text("Categorías:", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                        Row(modifier = Modifier.padding(top = 4.dp)) {
                            producto.categorias.forEach { cat ->
                                SuggestionChip(onClick = {}, label = { Text(cat) }, modifier = Modifier.padding(end = 8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Text("Stock Disponible: ${producto.stock ?: 0}", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}