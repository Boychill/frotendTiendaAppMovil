package com.api.frotendtiendaappmovil.di

import com.api.frotendtiendaappmovil.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Usamos runBlocking porque intercept es síncrono, pero leer DataStore es asíncrono.
        // Nota: DataStore cachea en memoria, así que esto es rápido después de la primera lectura.
        val token = runBlocking {
            tokenManager.token.first()
        }

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}