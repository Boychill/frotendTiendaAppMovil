package com.api.frotendtiendaappmovil.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Define puntos de ruptura (Breakpoints)
object WindowSize {
    const val COMPACT = 600
    const val MEDIUM = 840
}

@Composable
fun rememberWindowSize(): WindowType {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < WindowSize.COMPACT -> WindowType.Compact
        configuration.screenWidthDp < WindowSize.MEDIUM -> WindowType.Medium
        else -> WindowType.Expanded
    }
}

enum class WindowType { Compact, Medium, Expanded }

// Helper para obtener dimensiones (Objeto Estático para uso rápido)
object Dimens {
    val paddingSmall = 8.dp
    val paddingMedium = 16.dp
    val paddingLarge = 24.dp // <--- Faltaba este valor
    val buttonHeight = 48.dp
}

// Clase de datos para dimensiones dinámicas (Responsivas)
data class AppDimens(
    val paddingSmall: Dp,
    val paddingMedium: Dp,
    val paddingLarge: Dp, // <--- Agregado aquí también
    val buttonHeight: Dp
)

// Función para obtener dimensiones según el tamaño de pantalla
@Composable
fun getAppDimens(): AppDimens {
    val windowSize = rememberWindowSize()
    return when (windowSize) {
        WindowType.Compact -> AppDimens(
            paddingSmall = 8.dp,
            paddingMedium = 16.dp,
            paddingLarge = 24.dp, // Valor para celular
            buttonHeight = 48.dp
        )
        else -> AppDimens(
            paddingSmall = 12.dp,
            paddingMedium = 24.dp,
            paddingLarge = 32.dp, // Valor más grande para Tablet/Landscape
            buttonHeight = 56.dp
        )
    }
}