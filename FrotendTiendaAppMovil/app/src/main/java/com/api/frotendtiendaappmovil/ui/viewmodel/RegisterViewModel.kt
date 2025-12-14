package com.api.frotendtiendaappmovil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError = _nameError.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var registerSuccess by mutableStateOf(false)

    fun onRegisterClick() {
        _nameError.value = null
        _emailError.value = null
        _passwordError.value = null
        errorMessage = null

        var isValid = true

        if (!ValidationUtils.isValidName(firstName) || !ValidationUtils.isValidName(lastName)) {
            _nameError.value = "Nombres inválidos (min 3 letras, sin símbolos)"
            isValid = false
        }

        if (!ValidationUtils.isValidEmail(email)) {
            _emailError.value = "Formato de email incorrecto"
            isValid = false
        }

        if (!ValidationUtils.isValidStrongPassword(password)) {
            _passwordError.value = "Mínimo 6 caracteres, 1 mayúscula, 1 número"
            isValid = false
        }

        if (!isValid) return

        viewModelScope.launch {
            isLoading = true
            val result = authRepository.register(email, password, firstName, lastName)
            result.onSuccess {
                registerSuccess = true
            }.onFailure { error ->
                errorMessage = error.message ?: "Error al registrar"
            }
            isLoading = false
        }
    }
}