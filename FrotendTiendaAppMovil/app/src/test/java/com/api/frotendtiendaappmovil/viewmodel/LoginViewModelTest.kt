package com.api.frotendtiendaappmovil.ui.viewmodel

import com.api.frotendtiendaappmovil.data.repository.AuthRepository
import com.api.frotendtiendaappmovil.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<AuthRepository>()
    private val viewModel = LoginViewModel(repository)

    @Test
    fun `cuando email o password estan vacios, muestra mensaje de error`() {
        viewModel.email = ""
        viewModel.password = ""

        viewModel.onLoginClick()

        assertEquals("Por favor, llena todos los campos", viewModel.errorMessage)
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test
    fun `cuando login es exitoso, cambia estado loginSuccess a true`() {
        val emailTest = "test@correo.com"
        val passTest = "123456"
        viewModel.email = emailTest
        viewModel.password = passTest

        // CORRECCIÓN AQUÍ: Usamos true en lugar de Unit
        coEvery { repository.login(emailTest, passTest) } returns Result.success(true)

        viewModel.onLoginClick()

        assertTrue(viewModel.loginSuccess)
        assertEquals(null, viewModel.errorMessage)
    }

    @Test
    fun `cuando login falla, muestra mensaje de error`() {
        viewModel.email = "error@correo.com"
        viewModel.password = "malapass"
        val errorMsg = "Credenciales inválidas"

        coEvery { repository.login(any(), any()) } returns Result.failure(Exception(errorMsg))

        viewModel.onLoginClick()

        assertEquals(errorMsg, viewModel.errorMessage)
        assertFalse(viewModel.loginSuccess)
    }
}