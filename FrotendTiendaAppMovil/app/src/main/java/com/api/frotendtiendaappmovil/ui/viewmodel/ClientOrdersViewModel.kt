package com.api.frotendtiendaappmovil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.frotendtiendaappmovil.data.remote.PedidoDto
import com.api.frotendtiendaappmovil.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientOrdersViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    // Lista completa original
    private var allOrders = listOf<PedidoDto>()

    // Lista filtrada que ve la UI
    var filteredOrders by mutableStateOf<List<PedidoDto>>(emptyList())

    var searchQuery by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadMisPedidos()
    }

    fun loadMisPedidos() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = repository.getMisPedidos()

            result.onSuccess { lista ->
                allOrders = lista.reversed()
                filterOrders() // Aplicar filtro inicial (mostrar todos)
            }.onFailure { error ->
                errorMessage = error.message ?: "Error desconocido al cargar pedidos"
            }

            isLoading = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        filterOrders()
    }

    private fun filterOrders() {
        if (searchQuery.isBlank()) {
            filteredOrders = allOrders
        } else {
            filteredOrders = allOrders.filter { pedido ->
                // Buscamos por ID (ej: "a5c5") o por estado
                pedido.id.contains(searchQuery, ignoreCase = true) ||
                        (pedido.estado?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }
}