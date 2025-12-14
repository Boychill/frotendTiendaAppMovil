package com.api.frotendtiendaappmovil.data.repository

import com.api.frotendtiendaappmovil.data.local.TokenManager
import com.api.frotendtiendaappmovil.data.remote.ApiService
import com.api.frotendtiendaappmovil.data.remote.LoginRequest
import com.api.frotendtiendaappmovil.data.remote.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, pass: String): Result<Boolean> {
        return try {
            val response = apiService.login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                tokenManager.saveAuthData(body.token, body.role)
                Result.success(true)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CORRECCIÓN: Ahora recibe firstName y lastName
    suspend fun register(email: String, pass: String, firstName: String, lastName: String): Result<Boolean> {
        return try {
            val response = apiService.register(RegisterRequest(email, pass, firstName, lastName))
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                // Loguear el error para ver qué pasa
                Result.failure(Exception("Error registro: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}