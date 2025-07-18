package com.example.travelapp_kmp.network.auth

import com.example.travelapp_kmp.ApiConfig
import com.example.travelapp_kmp.models.SessionResponse
import com.example.travelapp_kmp.network.HttpClientProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

interface SessionRepository {
    suspend fun createSession(): SessionResponse
    suspend fun listSessions(): List<SessionResponse>
    suspend fun renameSession(sessionId: String, newName: String): SessionResponse
    suspend fun deleteSession(sessionId: String)
}

/**
 * @param token – JWT access token returned by /auth/login
 */
class SessionRepositoryImpl(
    private val token: String,
    private val client: HttpClient = HttpClientProvider.client
) : SessionRepository {

    /* --- endpoints ------------------------------------------------------ */
    // POST /auth/session   (create)
    // GET  /auth/sessions  (list)
    // PATCH|DELETE /auth/session/{id}/...
    private val sessionBase = "${ApiConfig.BASE_URL}/auth/session"
    private val sessionsUrl = "${ApiConfig.BASE_URL}/auth/sessions"

    /** Helper to add bearer to every call */
    private fun HttpRequestBuilder.auth() {
        header(HttpHeaders.Authorization, "Bearer $token")
    }

    override suspend fun createSession(): SessionResponse =
        client.post(sessionBase) {
            auth()
            contentType(ContentType.Application.Json)
        }.body()

    override suspend fun listSessions(): List<SessionResponse> =
        client.get(sessionsUrl) {
            auth()
        }.body()

    override suspend fun renameSession(sessionId: String, newName: String): SessionResponse =
        client.patch("$sessionBase/$sessionId/name") {
            auth()
            setBody(
                FormDataContent(Parameters.build { append("name", newName) })
            )
        }.body()

    override suspend fun deleteSession(sessionId: String) {
        client.delete("$sessionBase/$sessionId") { auth() }
    }
}
