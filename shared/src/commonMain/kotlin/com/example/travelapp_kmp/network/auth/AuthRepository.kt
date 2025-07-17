package com.example.travelapp_kmp.network.auth


import com.example.travelapp_kmp.ApiConfig
import com.example.travelapp_kmp.models.AuthResponse
import com.example.travelapp_kmp.models.RegisterRequest
import com.example.travelapp_kmp.models.TokenResponse
import com.example.travelapp_kmp.network.HttpClientProvider
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.*


interface AuthRepository {
    suspend fun register(email: String, password: String): AuthResponse
    suspend fun login(username: String, password: String): TokenResponse
}

class AuthRepositoryImpl(
    private val client: io.ktor.client.HttpClient = HttpClientProvider.client
) : AuthRepository {

    private val base = ApiConfig.BASE_URL

    override suspend fun register(
        email: String,
        password: String
    ): AuthResponse = client.post("$base/auth/register") {
        contentType(ContentType.Application.Json)
        setBody(RegisterRequest(email, password))
    }.body()

    override suspend fun login(
        username: String,
        password: String
    ): TokenResponse =              // <- return the real type
        client.post("${base}/auth/login") {
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("username", username)
                        append("password", password)
                        // FastAPI expects grant_type, even when empty
                        append("grant_type", "")
                    }
                )
            )
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
        }.body()

}