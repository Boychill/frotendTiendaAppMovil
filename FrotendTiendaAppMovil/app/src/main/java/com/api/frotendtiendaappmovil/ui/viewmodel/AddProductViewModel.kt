package com.api.frotendtiendaappmovil.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.data.repository.ProductRepository
import com.api.frotendtiendaappmovil.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    var nombre by mutableStateOf("")
    var precio by mutableStateOf("")
    var stock by mutableStateOf("")
    var categorias by mutableStateOf("")
    var selectedImageUri by mutableStateOf<Uri?>(null)

    var isLoading by mutableStateOf(false)
    var successMessage by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun uploadProduct(context: Context) {
        if (!ValidationUtils.isValidName(nombre)) {
            errorMessage = "Nombre inválido (min 3 caracteres)"
            return
        }
        if (!ValidationUtils.isValidPrice(precio)) {
            errorMessage = "Precio inválido (debe ser mayor a 0)"
            return
        }
        if (!ValidationUtils.isValidStock(stock)) {
            errorMessage = "Stock inválido (0-10000)"
            return
        }
        if (!ValidationUtils.isValidCategories(categorias)) {
            errorMessage = "Categorías inválidas (letras, números y comas)"
            return
        }
        if (selectedImageUri == null) {
            errorMessage = "La imagen es obligatoria"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            val catsList = categorias.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            val dto = ProductoDto(
                id = null,
                nombre = nombre.trim(),
                precio = precio.toDouble(),
                stock = stock.toInt(),
                imagenUrl = null,
                categorias = catsList,
            )

            val result = repository.crearProducto(context, dto, selectedImageUri!!)

            result.onSuccess {
                successMessage = "¡Producto creado exitosamente!"
                // Reset fields
                nombre = ""
                precio = ""
                stock = ""
                categorias = ""
                selectedImageUri = null
            }.onFailure {
                errorMessage = it.message ?: "Error al crear producto"
            }

            isLoading = false
        }
    }
}