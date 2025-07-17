package com.example.travelapp_kmp.network.chat

import com.example.travelapp_kmp.ApiConfig
import com.example.travelapp_kmp.models.ChatMessage
import com.example.travelapp_kmp.network.HttpClientProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface ChatRepository {
    /**
     * Send a user message → receive assistant reply.
     * Returns the assistant message as a [ChatMessage].
     */
    suspend fun sendMessage(sessionId: String, userText: String): ChatMessage
}

class ChatRepositoryImpl(
    private val token: String,
    private val client: HttpClient = HttpClientProvider.client
) : ChatRepository {

    override suspend fun sendMessage(sessionId: String, userText: String): ChatMessage =
        client.post("${ApiConfig.BASE_URL}/chat/$sessionId/messages") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(mapOf("content" to userText))
        }.body()
}
