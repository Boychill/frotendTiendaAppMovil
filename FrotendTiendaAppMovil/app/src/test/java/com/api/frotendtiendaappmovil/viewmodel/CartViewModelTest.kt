package com.api.frotendtiendaappmovil.ui.viewmodel

import android.location.Location
import com.api.frotendtiendaappmovil.data.remote.ApiService
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.data.repository.CartRepository
import com.api.frotendtiendaappmovil.util.LocationHelper
import com.api.frotendtiendaappmovil.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Usamos "relaxed = true" para no tener que configurar cada pequeña función del repo
    private val cartRepository = mockk<CartRepository>(relaxed = true)
    private val apiService = mockk<ApiService>()
    private val locationHelper = mockk<LocationHelper>()

    // Datos de prueba
    private val prod1 = ProductoDto("1", "Pizza", 10.0, null, 5, emptyList())

    // Simulamos que el repositorio tiene un StateFlow con 1 producto
    // Usamos MutableStateFlow porque tiene la propiedad .value que tu ViewModel usa
    private val fakeCartItems = MutableStateFlow(listOf(prod1))

    private fun setupViewModel(): CartViewModel {
        // Enseñamos al mock cómo comportarse
        every { cartRepository.items } returns fakeCartItems
        every { cartRepository.getTotal() } returns 10.0

        return CartViewModel(cartRepository, apiService, locationHelper)
    }

    @Test
    fun `obtenerUbicacionGps actualiza coordenadas cuando hay location`() = runTest {
        val viewModel = setupViewModel()

        // GIVEN - Simulamos una ubicación de Android (Lat: -33, Lng: -70)
        val mockLocation = mockk<Location>()
        every { mockLocation.latitude } returns -33.0
        every { mockLocation.longitude } returns -70.0

        // Cuando llamen al helper, devolvemos esa ubicación
        coEvery { locationHelper.getCurrentLocation() } returns mockLocation

        // WHEN
        viewModel.obtenerUbicacionGps()
        advanceUntilIdle() // Esperamos a que la corrutina termine

        // THEN
        assertTrue(viewModel.isGpsCaptured)
        assertEquals(-33.0, viewModel.gpsLat, 0.0)
        assertEquals(-70.0, viewModel.gpsLng, 0.0)
    }

    @Test
    fun `confirmarPedido falla si la direccion esta vacia`() {
        val viewModel = setupViewModel()

        // GIVEN - Dirección vacía
        viewModel.deliveryAddress = ""

        // WHEN
        viewModel.confirmarPedido()

        // THEN - Debe mostrar error y NO llamar a la API
        assertEquals("Por favor, escribe una dirección de envío", viewModel.errorMessage)
        coVerify(exactly = 0) { apiService.crearPedido(any()) }
    }

    @Test
    fun `confirmarPedido envia pedido a API y limpia carrito si es exitoso`() = runTest {
        val viewModel = setupViewModel()

        // GIVEN
        viewModel.deliveryAddress = "Calle Falsa 123"
        // Simulamos que la API responde "OK" (Response 200)
        coEvery { apiService.crearPedido(any()) } returns Response.success(mockk())

        // WHEN
        viewModel.confirmarPedido()
        advanceUntilIdle()

        // THEN
        assertTrue(viewModel.orderSuccess) // Flag de éxito
        assertEquals("", viewModel.deliveryAddress) // Se limpia la dirección

        // Verificamos que se llamó a limpiar carrito
        coVerify { cartRepository.clearCart() }
        // Verificamos que se llamó a la API 1 vez
        coVerify(exactly = 1) { apiService.crearPedido(any()) }
    }
}