package com.api.frotendtiendaappmovil.data.repository

import com.api.frotendtiendaappmovil.data.remote.ApiService
import com.api.frotendtiendaappmovil.data.remote.AddressDto
import com.api.frotendtiendaappmovil.data.remote.ProfileUpdateRequest
import com.api.frotendtiendaappmovil.data.remote.UserProfileDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getMyProfile(): Result<UserProfileDto> {
        return try {
            val response = apiService.getMyProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: No se pudo cargar el perfil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(nombre: String, apellido: String, telefono: String): Result<UserProfileDto> {
        return try {
            val request = ProfileUpdateRequest(nombre, apellido, telefono)
            val response = apiService.updateProfile(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addAddress(address: AddressDto): Result<AddressDto> {
        return try {
            val response = apiService.addAddress(address)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error guardando dirección: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteAddress(id: Long): Result<Boolean> {
        return try {
            val response = apiService.deleteAddress(id)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Error al eliminar"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateAddress(id: Long, address: AddressDto): Result<AddressDto> {
        return try {
            val response = apiService.updateAddress(id, address)
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Error al editar"))
        } catch (e: Exception) { Result.failure(e) }
    }
}