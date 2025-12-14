package com.api.frotendtiendaappmovil.data.repository

import com.api.frotendtiendaappmovil.data.remote.ApiService
import com.api.frotendtiendaappmovil.data.remote.PedidoDto
import com.api.frotendtiendaappmovil.data.remote.PedidoRequest // Importar DTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val apiService: ApiService
) {
    // --- ESTE ES EL MÉTODO QUE DEBES AGREGAR ---
    suspend fun crearPedido(request: PedidoRequest): Result<Boolean> {
        return try {
            val response = apiService.crearPedido(request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al crear pedido: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisPedidos(): Result<List<PedidoDto>> {
        return try {
            val response = apiService.getMisPedidos()
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar pedidos: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getAllPedidos(): Result<List<PedidoDto>> {
        return try {
            val response = apiService.getAllPedidos()
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Error: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateOrderStatus(pedidoId: String, nuevoEstado: String): Result<Boolean> {
        return try {
            val response = apiService.actualizarEstadoPedido(pedidoId, nuevoEstado)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Error al actualizar: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }
}