package com.api.frotendtiendaappmovil.ui.viewmodel

import android.content.Context
import android.net.Uri
import com.api.frotendtiendaappmovil.data.repository.ProductRepository
import com.api.frotendtiendaappmovil.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<ProductRepository>()
    private val viewModel = AddProductViewModel(repository)

    // Mocks de Android (Context y Uri) relajados para que no den error
    private val mockContext = mockk<Context>(relaxed = true)
    private val mockUri = mockk<Uri>(relaxed = true)

    @Test
    fun `uploadProduct falla si falta algun campo o la imagen`() {
        // GIVEN - Falta la imagen (es null)
        viewModel.nombre = "Nuevo Producto"
        viewModel.precio = "100"
        viewModel.categorias = "Test"
        viewModel.selectedImageUri = null

        // WHEN
        viewModel.uploadProduct(mockContext)

        // THEN
        assertEquals("Completa todos los campos e imagen", viewModel.errorMessage)

        // Verificamos que NUNCA se llame al repositorio
        coVerify(exactly = 0) { repository.crearProducto(any(), any(), any()) }
    }

    @Test
    fun `uploadProduct llama al repositorio cuando todo es valido`() = runTest {
        // GIVEN - Llenamos todos los campos correctamente
        viewModel.nombre = "Producto Real"
        viewModel.precio = "50.5"
        viewModel.stock = "10"
        viewModel.categorias = "Ropa, Verano"
        viewModel.selectedImageUri = mockUri

        // Simulamos que el repositorio responde exitosamente (true)
        coEvery {
            repository.crearProducto(any(), any(), any())
        } returns Result.success(true)

        // WHEN
        viewModel.uploadProduct(mockContext)
        advanceUntilIdle() // Esperamos a que la corrutina termine

        // THEN
        assertEquals("¡Producto creado!", viewModel.successMessage)
        assertNull(viewModel.errorMessage)

        // Validamos que los campos se limpien después del éxito
        assertEquals("", viewModel.nombre)
        assertNull(viewModel.selectedImageUri)

        // Verificamos que se llamó al repositorio 1 vez
        coVerify(exactly = 1) { repository.crearProducto(any(), any(), any()) }
    }
}