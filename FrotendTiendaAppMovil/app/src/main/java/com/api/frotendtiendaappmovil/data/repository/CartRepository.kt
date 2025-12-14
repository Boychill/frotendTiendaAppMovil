package com.api.frotendtiendaappmovil.data.repository

import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {

    private val _items = MutableStateFlow<List<ProductoDto>>(emptyList())
    val items: StateFlow<List<ProductoDto>> = _items.asStateFlow()

    // MODIFICADO: Retorna Boolean (true = agregado, false = sin stock)
    fun addToCart(producto: ProductoDto): Boolean {
        val currentList = _items.value
        val cantidadEnCarrito = currentList.count { it.id == producto.id }
        val stockMaximo = producto.stock ?: 0

        if (cantidadEnCarrito < stockMaximo) {
            _items.update { it + producto }
            return true
        } else {
            return false // No hay suficiente stock
        }
    }

    fun removeFromCart(producto: ProductoDto) {
        _items.update { currentList ->
            val index = currentList.indexOfFirst { it.id == producto.id }
            if (index != -1) {
                currentList.toMutableList().apply { removeAt(index) }
            } else {
                currentList
            }
        }
    }

    fun clearCart() { _items.value = emptyList() }

    fun getTotal(): Double { return _items.value.sumOf { it.precio } }
}