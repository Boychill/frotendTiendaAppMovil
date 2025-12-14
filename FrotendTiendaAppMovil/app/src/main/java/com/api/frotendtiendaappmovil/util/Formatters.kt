package com.api.frotendtiendaappmovil.util

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    fun formatPrice(price: Double): String {
        // Usamos Locale para Chile (u otro que use puntos para miles)
        val format = NumberFormat.getNumberInstance(Locale("es", "CL"))
        format.maximumFractionDigits = 0 // Sin decimales
        return "$${format.format(price)} CLP"
    }
}