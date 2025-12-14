package com.api.frotendtiendaappmovil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.frotendtiendaappmovil.data.remote.AddressDto
import com.api.frotendtiendaappmovil.data.remote.PedidoItemRequest
import com.api.frotendtiendaappmovil.data.remote.PedidoRequest
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.data.repository.CartRepository
import com.api.frotendtiendaappmovil.data.repository.OrderRepository
import com.api.frotendtiendaappmovil.data.repository.ProfileRepository
import com.api.frotendtiendaappmovil.util.LocationHelper
import com.api.frotendtiendaappmovil.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val locationService: LocationHelper,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    // Carrito reactivo
    val cartItems = cartRepository.items
    val total: Double get() = cartRepository.getTotal()

    // Estados del formulario
    var deliveryAddress by mutableStateOf("")
    var gpsLat by mutableDoubleStateOf(0.0)
    var gpsLng by mutableDoubleStateOf(0.0)
    var isGpsCaptured by mutableStateOf(false)

    // Lista de direcciones guardadas
    var savedAddresses by mutableStateOf<List<AddressDto>>(emptyList())

    // Estados de UI
    var isLoading by mutableStateOf(false)
    var orderSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // --- MÉTODOS DEL CARRITO ---

    fun addToCart(product: ProductoDto) = cartRepository.addToCart(product)

    fun removeItem(item: ProductoDto) = cartRepository.removeFromCart(item)

    fun clearCart() = cartRepository.clearCart()

    // --- MÉTODOS DE DIRECCIÓN Y GPS ---

    fun loadSavedAddresses() {
        viewModelScope.launch {
            profileRepository.getMyProfile().onSuccess { profile ->
                savedAddresses = profile.addresses
            }
        }
    }

    fun selectAddress(address: AddressDto) {
        // Concatenamos para mostrar algo útil en el campo de texto
        deliveryAddress = "${address.calle} #${address.numero}"

        // Si la dirección guardada tiene coordenadas válidas, las usamos
        if (address.latitud != 0.0 || address.longitud != 0.0) {
            gpsLat = address.latitud
            gpsLng = address.longitud
            isGpsCaptured = true
        }
    }

    fun obtenerUbicacionGps() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val location = locationService.getCurrentLocation()

            if (location != null) {
                gpsLat = location.latitude
                gpsLng = location.longitude
                isGpsCaptured = true
            } else {
                errorMessage = "No se pudo obtener la ubicación GPS. Verifica tus permisos."
            }
            isLoading = false
        }
    }

    fun clearGps() {
        gpsLat = 0.0
        gpsLng = 0.0
        isGpsCaptured = false
    }

    // --- CONFIRMACIÓN DEL PEDIDO ---

    fun confirmarPedido() {
        // 1. Validaciones
        if (cartItems.value.isEmpty()) {
            errorMessage = "El carrito está vacío"
            return
        }

        if (!ValidationUtils.isValidAddress(deliveryAddress)) {
            errorMessage = "Por favor, ingresa una dirección válida y detallada."
            return
        }

        // Validación GPS
        if (isGpsCaptured && !ValidationUtils.isValidGps(gpsLat, gpsLng)) {
            errorMessage = "Error: Coordenadas GPS inválidas (0,0). Intenta capturar de nuevo."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // 2. Preparar los datos
                val itemsMap = cartItems.value.groupingBy { it }.eachCount()
                val pedidoItems = itemsMap.mapNotNull { (prod, cantidad) ->
                    if (prod.id != null) {
                        PedidoItemRequest(
                            productId = prod.id,
                            nombre = prod.nombre,
                            cantidad = cantidad,
                            precio = prod.precio
                        )
                    } else null
                }

                val request = PedidoRequest(
                    precioTotal = total,
                    direccionEnvio = deliveryAddress.trim(),
                    latitud = gpsLat,
                    longitud = gpsLng,
                    items = pedidoItems
                )

                // 3. Llamada al Repositorio (IMPLEMENTACIÓN SOLICITADA)
                val result = orderRepository.crearPedido(request)

                result.onSuccess {
                    cartRepository.clearCart()
                    orderSuccess = true
                    deliveryAddress = ""
                    clearGps()
                }.onFailure { error ->
                    // VALIDACIÓN DE ERROR AMIGABLE
                    val mensaje = error.message ?: ""
                    if (mensaje.contains("400") || mensaje.contains("Stock")) {
                        errorMessage = "No se pudo crear el pedido. Verifica el stock de los productos."
                    } else if (mensaje.contains("404")) {
                        errorMessage = "Error de conexión o servicio no disponible."
                    } else {
                        errorMessage = "Ocurrió un error al procesar tu pedido. Intenta nuevamente."
                    }
                }

            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}