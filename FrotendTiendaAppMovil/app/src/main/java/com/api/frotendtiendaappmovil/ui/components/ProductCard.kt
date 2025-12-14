package com.api.frotendtiendaappmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.util.Formatters // Importamos el formateador

@Composable
fun ProductItem(
    producto: ProductoDto,
    onAddToCart: (Int) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    val currentStock = producto.stock ?: 0
    val isOutOfStock = currentStock <= 0

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(producto.imagenUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = producto.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)).background(Color.LightGray),
                    loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.Image, null, tint = Color.White) } },
                    error = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.BrokenImage, null, tint = Color.Red) } }
                )

                if (isOutOfStock) {
                    Box(
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AGOTADO", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else if (currentStock < 5) {
                    Box(modifier = Modifier.padding(8.dp).background(Color.Red, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp).align(Alignment.TopEnd)) {
                        Text("¡Solo quedan $currentStock!", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)

                // --- PRECIO FORMATEADO ---
                Text(
                    text = Formatters.formatPrice(producto.precio),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (!isOutOfStock) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f), RoundedCornerShape(8.dp))
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) { Icon(Icons.Default.Remove, "Menos", tint = MaterialTheme.colorScheme.primary) }
                        Text(text = quantity.toString(), fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if (quantity < currentStock) quantity++ }) { Icon(Icons.Default.Add, "Más", tint = MaterialTheme.colorScheme.primary) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = { onAddToCart(quantity); quantity = 1 },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isOutOfStock
                ) {
                    if (isOutOfStock) Text("Sin Stock")
                    else {
                        Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar")
                    }
                }
            }
        }
    }
}