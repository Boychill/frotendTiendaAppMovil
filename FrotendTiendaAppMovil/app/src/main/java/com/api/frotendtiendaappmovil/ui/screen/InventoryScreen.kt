package com.api.frotendtiendaappmovil.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search // Importante para el ícono
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.ui.components.BackButton
import com.api.frotendtiendaappmovil.ui.components.ProductDetailDialog
import com.api.frotendtiendaappmovil.ui.viewmodel.InventoryViewModel
import com.api.frotendtiendaappmovil.util.Dimens
import com.api.frotendtiendaappmovil.util.Formatters

@Composable
fun InventoryScreen(
    navController: NavController,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedProduct by remember { mutableStateOf<ProductoDto?>(null) }

    if (selectedProduct != null) {
        ProductDetailDialog(producto = selectedProduct!!, onDismiss = { selectedProduct = null })
    }

    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    // Auto-refresh al entrar
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .padding(top = 40.dp, start = Dimens.paddingSmall, bottom = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButton(onClick = { navController.popBackStack() })
                    Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                    Text("Inventario (Admin)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                // Botón refresh eliminado por auto-refresh
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("admin/add-product") }) {
                Icon(Icons.Default.Add, "Nuevo")
            }
        }
    ) { padding ->
        // Cambiado a Column para apilar el buscador y la lista
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // --- BARRA DE BÚSQUEDA IMPLEMENTADA ---
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar producto...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp)
            )

            // Contenedor de la lista
            Box(modifier = Modifier.weight(1f)) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else if (viewModel.products.isEmpty()) {
                    Text("No hay productos", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(Dimens.paddingMedium),
                        verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
                    ) {
                        items(viewModel.products) { producto ->
                            InventoryItem(
                                producto = producto,
                                onClick = { selectedProduct = producto },
                                onEdit = { navController.navigate("admin/edit-product/${producto.id}") },
                                onDelete = { viewModel.deleteProduct(producto) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTE AUXILIAR ---
@Composable
fun InventoryItem(
    producto: ProductoDto,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.paddingSmall)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(Dimens.paddingMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = Formatters.formatPrice(producto.precio),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Stock: ${producto.stock ?: 0}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if ((producto.stock ?: 0) > 0) Color(0xFF4CAF50) else Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Editar", tint = Color.Blue)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red)
            }
        }
    }
}