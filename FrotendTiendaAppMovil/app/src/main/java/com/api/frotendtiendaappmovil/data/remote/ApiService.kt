package com.api.frotendtiendaappmovil.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==========================================
    // MICROSERVICIO: AUTH (Autenticación)
    // ==========================================
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Void>


    // ==========================================
    // MICROSERVICIO: CATÁLOGO (Productos)
    // ==========================================
    @GET("catalogo")
    suspend fun getProductos(): Response<List<ProductoDto>>

    @GET("catalogo/{id}")
    suspend fun getProductoById(@Path("id") id: String): Response<ProductoDto>

    @Multipart
    @POST("catalogo")
    suspend fun crearProducto(
        @Part("product") product: RequestBody,
        @Part imagen: MultipartBody.Part
    ): Response<ProductoDto>

    // Actualizar SOLO DATOS (Sin imagen nueva)
    @PUT("catalogo/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body product: ProductoDto
    ): Response<ProductoDto>

    // Actualizar CON IMAGEN (Multipart)
    @Multipart
    @PUT("catalogo/{id}")
    suspend fun updateProductWithImage(
        @Path("id") id: String,
        @Part("product") product: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ProductoDto>

    @DELETE("catalogo/{id}")
    suspend fun eliminarProducto(@Path("id") id: String): Response<Void>

    @PUT("catalogo/stock/{id}")
    suspend fun actualizarStock(@Path("id") id: String, @Query("stock") stock: Int): Response<ProductoDto>


    // ==========================================
    // MICROSERVICIO: PEDIDOS
    // ==========================================
    @POST("pedidos")
    suspend fun crearPedido(@Body request: PedidoRequest): Response<Void>

    @GET("pedidos/mis-pedidos")
    suspend fun getMisPedidos(): Response<List<PedidoDto>>

    @GET("pedidos/todos")
    suspend fun getAllPedidos(): Response<List<PedidoDto>>

    @PUT("pedidos/{id}/estado")
    suspend fun actualizarEstadoPedido(
        @Path("id") pedidoId: String,
        @Query("status") nuevoEstado: String
    ): Response<Void>


    // ==========================================
    // MICROSERVICIO: PERFIL (Usuarios y Direcciones)
    // ==========================================
    @GET("perfil/mi-perfil")
    suspend fun getMyProfile(): Response<UserProfileDto>

    @PUT("perfil/actualizar")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): Response<UserProfileDto>

    @POST("perfil/direcciones")
    suspend fun addAddress(@Body address: AddressDto): Response<AddressDto>

    @DELETE("perfil/direcciones/{id}")
    suspend fun deleteAddress(@Path("id") id: Long): Response<Void>

    @PUT("perfil/direcciones/{id}")
    suspend fun updateAddress(@Path("id") id: Long, @Body address: AddressDto): Response<AddressDto>
}

// ==========================================
// DATA TRANSFER OBJECTS (DTOs)
// ==========================================

// --- Auth ---
data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

data class AuthResponse(
    val token: String,
    val role: String,
    val userId: String // Útil para guardar el ID localmente si se necesita
)

// --- Catálogo ---
data class ProductoDto(
    val id: String?,
    val nombre: String,
    val precio: Double,
    @SerializedName("imageUrl") val imagenUrl: String?, // Mapeo si el backend manda "imageUrl" o "imagenUrl"
    val stock: Int? = 0,
    val categorias: List<String>? = emptyList()
)

// --- Pedidos ---
data class PedidoRequest(
    val precioTotal: Double,
    val direccionEnvio: String,
    val latitud: Double,
    val longitud: Double,
    val items: List<PedidoItemRequest>
)

data class PedidoItemRequest(
    val productId: String,
    val nombre: String,
    val cantidad: Int,
    val precio: Double
)

data class PedidoDto(
    val id: String,
    @SerializedName("status") val estado: String?,
    @SerializedName("precioTotal") val total: Double?,
    @SerializedName("createdAt") val fecha: String?,
    val direccionEnvio: String?,
    val items: List<PedidoItemDto>?,
    val latitud: Double? = 0.0,
    val longitud: Double? = 0.0
)

data class PedidoItemDto(
    val nombre: String,
    val cantidad: Int,
    val precio: Double
)

// --- Perfil ---
data class ProfileUpdateRequest(
    val nombre: String,
    val apellido: String,
    val telefono: String
)

data class AddressDto(
    val id: Long? = null,
    val alias: String, // Ej: "Casa", "Trabajo"
    val calle: String,
    val numero: String,
    val comuna: String? = "",
    val latitud: Double,
    val longitud: Double
)

data class UserProfileDto(
    val userId: String,
    val email: String,
    val nombre: String?,
    val apellido: String?,
    val telefono: String?,
    // CORRECCIÓN: Aquí es donde se cortaba tu código anterior
    val addresses: List<AddressDto> = emptyList()
)