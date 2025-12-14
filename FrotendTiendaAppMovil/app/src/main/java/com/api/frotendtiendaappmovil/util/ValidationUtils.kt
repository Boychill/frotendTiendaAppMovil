package com.api.frotendtiendaappmovil.util

import android.util.Patterns

object ValidationUtils {

    // --- AUTENTICACIÓN ---

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // VaPassword123lidación simple (usada en Login si se desea, o para chequeos rápidos)
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    // Validación FUERTE (Usada en Registro): 6 chars, 1 Mayúscula, 1 Número
    fun isValidStrongPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[0-9])(?=.*[A-Z]).{6,}$")
        return password.matches(regex)
    }

    // Validar Nombres (Solo letras y espacios, evita "User123")
    fun isValidName(text: String): Boolean {
        // Permite letras, tildes y espacios
        val regex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
        return text.trim().length >= 3 && text.matches(regex)
    }

    // --- PRODUCTOS ---
    fun isValidProductName(text: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s\\.\\-]+$")
        return text.trim().length >= 3 && text.matches(regex)
    }

    fun isValidPrice(price: String): Boolean {
        val p = price.toDoubleOrNull()
        // Debe ser positivo y un valor razonable
        return p != null && p > 0 && p < 100_000_000
    }

    fun isValidStock(stock: String): Boolean {
        val s = stock.toIntOrNull()
        return s != null && s >= 0 && s < 100_000
    }

    fun isValidCategories(text: String): Boolean {
        // Solo letras, números, espacios y comas
        val regex = Regex("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s,]+$")
        return text.isNotBlank() && text.matches(regex)
    }
    fun isValidAddress(address: String): Boolean {
        return address.trim().length >= 5 // Evita direcciones vacías o muy cortas
    }
    fun isValidPhone(phone: String): Boolean {
        // Permite opcionalmente un '+' al inicio, seguido de 8 a 15 números
        val regex = Regex("^\\+?[0-9]{8,15}$")
        return phone.matches(regex)
    }
    fun isValidGps(lat: Double, lng: Double): Boolean {
        // Valida que no sea el valor por defecto (0.0, 0.0)
        return lat != 0.0 || lng != 0.0
    }

    // Genérico
    fun isValidText(text: String): Boolean {
        return text.isNotBlank()
    }
}