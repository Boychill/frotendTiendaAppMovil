package com.api.frotendtiendaappmovil.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.data.repository.CartRepository
import com.api.frotendtiendaappmovil.ui.components.ProductDetailDialog
import com.api.frotendtiendaappmovil.ui.components.ProductItem
import com.api.frotendtiendaappmovil.ui.viewmodel.CatalogViewModel
import com.api.frotendtiendaappmovil.util.Dimens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navController: NavController,
    viewModel: CatalogViewModel = hiltViewModel(),
    cartRepository: CartRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val cartItems by cartRepository.items.collectAsState()

    val userRole = viewModel.userRole
    var selectedProduct by remember { mutableStateOf<ProductoDto?>(null) }

    if (selectedProduct != null) {
        ProductDetailDialog(producto = selectedProduct!!, onDismiss = { selectedProduct = null })
    }

    LaunchedEffect(Unit) { viewModel.loadProducts() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(Dimens.paddingMedium))
                Text("Menú Tienda", modifier = Modifier.padding(Dimens.paddingMedium), style = MaterialTheme.typography.headlineSmall)
                if (userRole != null && userRole != "CLIENTE") {
                    Text("Rol: $userRole", modifier = Modifier.padding(horizontal = Dimens.paddingMedium), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Catálogo") },
                    selected = true,
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                    onClick = { scope.launch { drawerState.close() } }
                )

                // --- MODIFICACIÓN: Ocultar si es ADMIN ---
                if (userRole != "ADMIN" && userRole != "DESPACHADOR") {
                    NavigationDrawerItem(
                        label = { Text("Mi Perfil") },
                        selected = false,
                        icon = { Icon(Icons.Default.Person, null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("client/profile")
                        }
                    )
                }

                val role = userRole?.uppercase() ?: ""
                if (role != "ADMIN" && role != "DESPACHADOR") {
                    NavigationDrawerItem(
                        label = { Text("Mis Pedidos") },
                        selected = false,
                        icon = { Icon(Icons.Default.ShoppingBag, null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("client/orders")
                        }
                    )
                }

                if (role == "ADMIN") {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Gestión", modifier = Modifier.padding(start = 16.dp), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    NavigationDrawerItem(
                        label = { Text("Inventario") },
                        selected = false,
                        icon = { Icon(Icons.Default.Inventory, null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("admin/inventory")
                        }
                    )
                }

                if (role == "ADMIN" || role == "DESPACHADOR") {
                    NavigationDrawerItem(
                        label = { Text("Envíos") },
                        selected = false,
                        icon = { Icon(Icons.Default.LocalShipping, null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("admin/orders")
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                    onClick = {
                        viewModel.logout { navController.navigate("login") { popUpTo("catalogo") { inclusive = true } } }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Catálogo") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, actionIconContentColor = Color.White),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menú", tint = Color.White) }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            BadgedBox(badge = { if (cartItems.isNotEmpty()) Badge { Text(cartItems.size.toString()) } }) {
                                Icon(Icons.Default.ShoppingCart, "Carrito")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth().padding(Dimens.paddingMedium),
                    placeholder = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = if (viewModel.searchQuery.isNotEmpty()) { { IconButton(onClick = { viewModel.onSearchQueryChanged("") }) { Icon(Icons.Default.Close, null) } } } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )

                if (viewModel.categories.isNotEmpty()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = Dimens.paddingMedium, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
                    ) {
                        item { FilterChip(selected = viewModel.selectedCategory == null, onClick = { viewModel.selectCategory(null) }, label = { Text("Todos") }) }
                        items(viewModel.categories.size) { index ->
                            val category = viewModel.categories[index]
                            FilterChip(selected = viewModel.selectedCategory == category, onClick = { viewModel.selectCategory(category) }, label = { Text(category.replaceFirstChar { it.uppercase() }) })
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (viewModel.isLoading) CircularProgressIndicator(Modifier.align(Alignment.Center))
                    else if (viewModel.errorMessage != null) Text(viewModel.errorMessage!!, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    else {
                        if (viewModel.products.isEmpty()) {
                            Text("No hay productos", modifier = Modifier.align(Alignment.Center))
                        } else {
                            LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(Dimens.paddingSmall)) {
                                items(viewModel.products) { producto ->
                                    Box(modifier = Modifier.clickable{ selectedProduct = producto }) {
                                        ProductItem(producto = producto, onAddToCart = { cantidad ->
                                            repeat(cantidad) { cartRepository.addToCart(producto) }
                                            Toast.makeText(context, "Agregado", Toast.LENGTH_SHORT).show()
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}