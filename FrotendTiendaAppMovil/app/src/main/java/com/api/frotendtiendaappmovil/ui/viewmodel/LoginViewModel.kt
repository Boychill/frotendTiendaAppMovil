package com.api.frotendtiendaappmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.frotendtiendaappmovil.data.repository.AuthRepository
import com.api.frotendtiendaappmovil.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // 1. Limpiar errores
        _emailError.value = null
        _passwordError.value = null

        // 2. Validar
        var isValid = true

        if (!ValidationUtils.isValidEmail(email)) {
            _emailError.value = "Correo inválido (ej: usuario@mail.com)"
            isValid = false
        }

        if (pass.isEmpty()) {
            _passwordError.value = "Ingresa tu contraseña"
            isValid = false
        }

        if (!isValid) return

        // 3. Llamada al Repositorio
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.login(email, pass)

            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                onError(e.message ?: "Credenciales incorrectas")
            }

            _isLoading.value = false
        }
    }
}