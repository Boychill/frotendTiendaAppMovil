package com.api.frotendtiendaappmovil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.api.frotendtiendaappmovil.data.repository.CartRepository
// Importamos todas las pantallas de una vez para evitar listas largas
import com.api.frotendtiendaappmovil.ui.screen.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    cartRepository: CartRepository
) {
    NavHost(navController = navController, startDestination = "login") {

        // --- AUTH ---
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }

        // --- CLIENTE ---
        composable("catalogo") { CatalogScreen(navController = navController, cartRepository = cartRepository) }
        composable("cart") { CartScreen(navController = navController) }

        // Rutas de Cliente agregadas:
        composable("client/orders") { ClientOrdersScreen(navController = navController) }

        // ¡ESTA ES LA QUE FALTABA! Sin esto, el menú "Mi Perfil" fallará.
        composable("client/profile") { ProfileScreen(navController = navController) }

        // --- ADMIN ---
        composable("admin/add-product") { AddProductScreen(navController = navController) }
        composable("admin/orders") { AdminOrdersScreen(navController = navController) }
        composable("admin/inventory") { InventoryScreen(navController = navController) }

        // --- EDICIÓN (Ruta con Argumento) ---
        composable(
            route = "admin/edit-product/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            EditProductScreen(
                navController = navController,
                productId = productId
            )
        }
    }
}