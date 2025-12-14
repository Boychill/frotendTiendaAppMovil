package com.api.frotendtiendaappmovil.ui.viewmodel

import com.api.frotendtiendaappmovil.data.local.TokenManager
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.api.frotendtiendaappmovil.data.repository.ProductRepository
import com.api.frotendtiendaappmovil.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<ProductRepository>()
    private val tokenManager = mockk<TokenManager>()

    // Datos de prueba para reutilizar
    private val prod1 = ProductoDto("1", "Zapatillas Nike", 100.0, null, 10, listOf("Calzado", "Deporte"))
    private val prod2 = ProductoDto("2", "Camiseta Adidas", 50.0, null, 20, listOf("Ropa", "Deporte"))
    private val prod3 = ProductoDto("3", "Gorra", 15.0, null, 5, listOf("Accesorios"))
    private val listaPrueba = listOf(prod1, prod2, prod3)

    private fun setupViewModel(): CatalogViewModel {
        // Configuramos los Mocks para que respondan algo cuando el ViewModel arranque (init)
        coEvery { tokenManager.role } returns flowOf("user") // Simulamos que es un usuario normal
        coEvery { repository.getProductos() } returns Result.success(listaPrueba) // Simulamos la API

        return CatalogViewModel(repository, tokenManager)
    }

    @Test
    fun `al iniciar, carga productos y extrae categorias unicas`() = runTest {
        // GIVEN (Configuración en setupViewModel)
        val viewModel = setupViewModel()

        // Esperamos a que corran las corrutinas del init
        advanceUntilIdle()

        // THEN
        assertEquals(3, viewModel.products.size)
        // Las categorías deben estar ordenadas y sin repetidos: "Accesorios", "Calzado", "Deporte", "Ropa"
        assertEquals(4, viewModel.categories.size)
        assertEquals(listOf("Accesorios", "Calzado", "Deporte", "Ropa"), viewModel.categories)
    }

    @Test
    fun `buscador filtra productos por nombre`() = runTest {
        val viewModel = setupViewModel()
        advanceUntilIdle()

        // WHEN (El usuario escribe "Nike")
        viewModel.onSearchQueryChanged("Nike")

        // THEN (Solo debe quedar 1 producto)
        assertEquals(1, viewModel.products.size)
        assertEquals("Zapatillas Nike", viewModel.products.first().nombre)
    }

    @Test
    fun `seleccionar categoria filtra productos`() = runTest {
        val viewModel = setupViewModel()
        advanceUntilIdle()

        // WHEN (El usuario selecciona la categoría "Deporte")
        viewModel.selectCategory("Deporte")

        // THEN (Deben quedar 2 productos: Zapatillas y Camiseta)
        assertEquals(2, viewModel.products.size)
    }

    @Test
    fun `combinar busqueda y categoria filtra correctamente`() = runTest {
        val viewModel = setupViewModel()
        advanceUntilIdle()

        // WHEN (Selecciona "Deporte" Y busca "Camiseta")
        viewModel.selectCategory("Deporte")
        viewModel.onSearchQueryChanged("Camiseta")

        // THEN (Solo debe quedar la Camiseta, aunque las Zapatillas también sean de Deporte)
        assertEquals(1, viewModel.products.size)
        assertEquals("Camiseta Adidas", viewModel.products.first().nombre)
    }
}