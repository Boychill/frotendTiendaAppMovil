package com.api.frotendtiendaappmovil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private var allProducts = listOf<ProductoDto>() // Respaldo
    var products by mutableStateOf<List<ProductoDto>>(emptyList()) // Visible
    var searchQuery by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.getProductos()
            result.onSuccess { lista ->
                allProducts = lista
                filterProducts()
            }.onFailure { errorMessage = "Error cargando inventario" }
            isLoading = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        filterProducts()
    }

    private fun filterProducts() {
        if (searchQuery.isBlank()) {
            products = allProducts
        } else {
            products = allProducts.filter {
                it.nombre.contains(searchQuery, ignoreCase = true) ||
                        it.id?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }

    fun deleteProduct(product: ProductoDto) {
        val prodId = product.id ?: return
        viewModelScope.launch {
            isLoading = true
            val result = repository.deleteProducto(prodId)
            result.onSuccess {
                allProducts = allProducts.filter { it.id != prodId }
                filterProducts()
                successMessage = "Producto eliminado"
            }.onFailure { error ->
                errorMessage = "No se pudo eliminar: ${error.message}"
            }
            isLoading = false
        }
    }

    fun updateProductStock(product: ProductoDto, newStock: Int) {
        val prodId = product.id ?: return
        viewModelScope.launch {
            isLoading = true
            val result = repository.updateStock(prodId, newStock)
            result.onSuccess {
                successMessage = "Stock actualizado a $newStock"
                loadProducts()
            }.onFailure { error ->
                errorMessage = "Error: ${error.message}"
            }
            isLoading = false
        }
    }

    fun clearMessages() { successMessage = null; errorMessage = null }
}