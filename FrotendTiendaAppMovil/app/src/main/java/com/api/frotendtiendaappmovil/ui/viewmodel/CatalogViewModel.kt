package com.api.frotendtiendaappmovil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.frotendtiendaappmovil.data.local.TokenManager
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private var allProducts = listOf<ProductoDto>()
    var products by mutableStateOf<List<ProductoDto>>(emptyList())
    var categories by mutableStateOf<List<String>>(emptyList())
    var selectedCategory by mutableStateOf<String?>(null)
    var searchQuery by mutableStateOf("")

    // Estado del Rol
    var userRole by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadUserRole()
        loadProducts()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            // Usamos collectLatest para escuchar actualizaciones del DataStore en tiempo real
            tokenManager.role.collectLatest { role ->
                userRole = role
                // Log para depuración (verás esto en Logcat)
                println("DEBUG: Rol cargado en Catalogo -> $userRole")
            }
        }
    }

    // ... (El resto de funciones: loadProducts, filters, logout siguen igual) ...
    fun loadProducts() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.getProductos()
            result.onSuccess { lista ->
                allProducts = lista
                val cats = mutableSetOf<String>()
                lista.forEach { p -> p.categorias?.forEach { c -> cats.add(c) } }
                categories = cats.toList().sorted()
                applyFilter()
            }.onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun selectCategory(category: String?) {
        selectedCategory = category
        applyFilter()
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        applyFilter()
    }

    private fun applyFilter() {
        var filtered = allProducts
        if (selectedCategory != null) {
            filtered = filtered.filter { it.categorias?.contains(selectedCategory) == true }
        }
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.nombre.contains(searchQuery, ignoreCase = true)
            }
        }
        products = filtered
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearAuthData()
            onLogout()
        }
    }
}