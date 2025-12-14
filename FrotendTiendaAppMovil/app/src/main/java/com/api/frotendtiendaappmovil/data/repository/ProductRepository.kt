package com.api.frotendtiendaappmovil.data.repository

import android.content.Context
import android.net.Uri
import com.api.frotendtiendaappmovil.data.remote.ApiService
import com.api.frotendtiendaappmovil.data.remote.ProductoDto
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val gson = Gson() // Para convertir a JSON

    // ... (getProductos, deleteProducto, etc. igual) ...
    suspend fun getProductos(): Result<List<ProductoDto>> { try { val r = apiService.getProductos(); return if (r.isSuccessful && r.body() != null) Result.success(r.body()!!) else Result.failure(Exception("Error: ${r.code()}")) } catch (e: Exception) { return Result.failure(e) } }
    suspend fun deleteProducto(id: String): Result<Boolean> { try { val r = apiService.eliminarProducto(id); return if (r.isSuccessful) Result.success(true) else Result.failure(Exception("Error: ${r.code()}")) } catch (e: Exception) { return Result.failure(e) } }
    suspend fun updateStock(id: String, s: Int): Result<Boolean> { try { val r = apiService.actualizarStock(id, s); return if (r.isSuccessful) Result.success(true) else Result.failure(Exception("Error: ${r.code()}")) } catch (e: Exception) { return Result.failure(e) } }
    suspend fun getProductById(id: String): Result<ProductoDto> { try { val r = apiService.getProductos(); if (r.isSuccessful && r.body() != null) { val p = r.body()!!.find { it.id == id }; return if (p != null) Result.success(p) else Result.failure(Exception("404")) } else { return Result.failure(Exception("Error: ${r.code()}")) } } catch (e: Exception) { return Result.failure(e) } }

    // --- CREAR con JSON + Imagen ---
    suspend fun crearProducto(context: Context, dto: ProductoDto, imageUri: Uri): Result<Boolean> {
        return try {
            // 1. Serializar DTO a JSON
            val jsonString = gson.toJson(dto)
            val productBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

            // 2. Preparar Imagen
            val file = getFileFromUri(context, imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagenPart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = apiService.crearProducto(productBody, imagenPart)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Error ${response.code()}: ${response.errorBody()?.string()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- ACTUALIZAR con JSON + Imagen (Opcional) ---
    suspend fun updateProductWithImage(context: Context, id: String, dto: ProductoDto, imageUri: Uri?): Result<Boolean> {
        return try {
            val response = if (imageUri != null) {
                // 1. SI HAY IMAGEN: Usamos Multipart
                val jsonString = gson.toJson(dto)
                val productBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                val file = getFileFromUri(context, imageUri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagenPart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                // Llamada a endpoint Multipart
                apiService.updateProductWithImage(id, productBody, imagenPart)
            } else {
                // 2. NO HAY IMAGEN: Usamos JSON normal
                // Llamada a endpoint JSON
                apiService.updateProduct(id, dto)
            }

            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Error ${response.code()}: ${response.errorBody()?.string()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input -> FileOutputStream(file).use { output -> input.copyTo(output) } }
        return file
    }
}