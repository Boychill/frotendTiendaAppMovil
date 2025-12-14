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
class AdminOrdersViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    private var allOrders = listOf<PedidoDto>() // Lista completa de respaldo
    var orders by mutableStateOf<List<PedidoDto>>(emptyList()) // Lista visible filtrada
    var searchQuery by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    init {
        loadAllOrders()
    }

    fun loadAllOrders() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.getAllPedidos()
            result.onSuccess { lista ->
                allOrders = lista.reversed()
                filterOrders()
            }.onFailure { error ->
                errorMessage = "Error cargando pedidos: ${error.message}"
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
            orders = allOrders
        } else {
            orders = allOrders.filter {
                it.id.contains(searchQuery, ignoreCase = true) ||
                        (it.estado?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    fun updateStatus(pedido: PedidoDto, nuevoEstado: String) {
        viewModelScope.launch {
            isLoading = true
            val estadoUpper = nuevoEstado.uppercase()
            val result = repository.updateOrderStatus(pedido.id, estadoUpper)
            result.onSuccess {
                successMessage = "Pedido actualizado a $estadoUpper"
                loadAllOrders()
            }.onFailure { error ->
                errorMessage = "No se pudo actualizar: ${error.message}"
            }
            isLoading = false
        }
    }

    fun clearMessages() { errorMessage = null; successMessage = null }
}