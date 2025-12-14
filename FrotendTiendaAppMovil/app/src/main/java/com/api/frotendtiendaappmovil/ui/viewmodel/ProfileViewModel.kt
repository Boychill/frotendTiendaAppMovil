package com.api.frotendtiendaappmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.frotendtiendaappmovil.data.remote.AddressDto
import com.api.frotendtiendaappmovil.data.remote.UserProfileDto
import com.api.frotendtiendaappmovil.data.repository.ProfileRepository
import com.api.frotendtiendaappmovil.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfileDto?>(null)
    val userProfile: StateFlow<UserProfileDto?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getMyProfile()
            result.onSuccess { _userProfile.value = it }
                .onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateUserData(nombre: String, apellido: String, telefono: String) {
        // 1. Validar Nombre
        if (nombre.isBlank() || !ValidationUtils.isValidName(nombre)) {
            _errorMessage.value = "Nombre inválido (no puede estar vacío ni tener números)"
            return
        }
        // 2. Validar Apellido
        if (apellido.isBlank() || !ValidationUtils.isValidName(apellido)) {
            _errorMessage.value = "Apellido inválido (no puede estar vacío ni tener números)"
            return
        }
        // 3. Validar Teléfono (NUEVO)
        if (!ValidationUtils.isValidPhone(telefono)) {
            _errorMessage.value = "Teléfono inválido (solo números, min 8 dígitos)"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateProfile(nombre.trim(), apellido.trim(), telefono.trim())

            result.onSuccess {
                _userProfile.value = it
                _errorMessage.value = null // Limpiar error si hubo éxito
            }.onFailure {
                _errorMessage.value = it.message
            }

            _isLoading.value = false
        }
    }

    // ... (El resto de funciones saveAddress y deleteAddress se mantienen igual) ...
    fun saveAddress(alias: String, calle: String, numero: String, lat: Double, lng: Double, id: Long? = null) {
        if (alias.isBlank()) {
            _errorMessage.value = "El alias es obligatorio"
            return
        }
        val hasGps = (lat != 0.0 || lng != 0.0)
        if (!hasGps && (calle.isBlank() || numero.isBlank())) {
            _errorMessage.value = "Debes ingresar Calle y Número, o usar el GPS."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val addressDto = AddressDto(
                id = id,
                alias = alias,
                calle = if (calle.isBlank()) "Ubicación GPS" else calle,
                numero = if (numero.isBlank()) "S/N" else numero,
                comuna = "",
                latitud = lat,
                longitud = lng
            )
            val result = if (id == null) repository.addAddress(addressDto) else repository.updateAddress(id, addressDto)
            result.onSuccess { loadProfile() }.onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteAddress(id)
            result.onSuccess { loadProfile() }.onFailure { _errorMessage.value = "Error: ${it.message}" }
            _isLoading.value = false
        }
    }
}