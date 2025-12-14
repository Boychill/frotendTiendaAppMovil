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
class EditProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    var id by mutableStateOf("")
    var nombre by mutableStateOf("")
    var precio by mutableStateOf("")
    var stock by mutableStateOf("")
    var categorias by mutableStateOf("")
    var currentImageUrl by mutableStateOf("")
    var newImageUri by mutableStateOf<Uri?>(null)

    var isLoading by mutableStateOf(false)
    var successMessage by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadProduct(productId: String) {
        if (id.isNotEmpty() && id == productId) return

        viewModelScope.launch {
            isLoading = true
            val result = repository.getProductById(productId)
            result.onSuccess {
                id = it.id ?: ""
                nombre = it.nombre
                precio = it.precio.toString()
                stock = it.stock.toString()
                currentImageUrl = it.imagenUrl ?: ""
                categorias = it.categorias?.joinToString(", ") ?: ""
            }.onFailure {
                errorMessage = "Error cargando: ${it.message}"
            }
            isLoading = false
        }
    }

    fun onImageSelected(uri: Uri?) { newImageUri = uri }

    fun updateProduct(context: Context) {
        if (!ValidationUtils.isValidProductName(nombre)) {
            errorMessage = "Nombre inválido (min 3 letras/números)"
            return
        }
        if (!ValidationUtils.isValidPrice(precio)) { errorMessage = "Precio inválido"; return }
        if (!ValidationUtils.isValidStock(stock)) { errorMessage = "Stock inválido"; return }
        if (!ValidationUtils.isValidCategories(categorias)) { errorMessage = "Categorías inválidas"; return }

        viewModelScope.launch {
            isLoading = true
            val catsList = categorias.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val dto = ProductoDto(id, nombre.trim(), precio.toDouble(), currentImageUrl, stock.toInt(), catsList)

            repository.updateProductWithImage(context, id, dto, newImageUri)
                .onSuccess { successMessage = "Producto actualizado" }
                .onFailure { errorMessage = it.message }

            isLoading = false
        }
    }
}