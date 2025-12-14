package com.api.frotendtiendaappmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.api.frotendtiendaappmovil.data.repository.CartRepository
import com.api.frotendtiendaappmovil.ui.navigation.AppNavigation
import com.api.frotendtiendaappmovil.ui.theme.FrotendTiendaAppMovilTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var cartRepository: CartRepository // Inyección directa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrotendTiendaAppMovilTheme {
                val navController = rememberNavController()
                // Pasamos el repositorio a la navegación
                AppNavigation(navController = navController, cartRepository = cartRepository)
            }
        }
    }
}