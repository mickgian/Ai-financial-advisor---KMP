package com.example.travelapp_kmp.network.chat

import com.example.travelapp_kmp.ApiConfig
import com.example.travelapp_kmp.network.HttpClientProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface ChatRepository {
    /**
     * Send one user message to the backend and return **only** the assistant’s
     * reply text (the view-model converts it to a ChatMessage later).
     *
     * @param sessionId – ID of the current chat session (ignored by the
     *                   default impl because the backend knows it from the JWT,
     *                   but keeping the parameter lets us pass it to any
     *                   alternative implementation that might need it).
     * @param userText  – the message the user typed.
     */
    suspend fun sendMessage(sessionId: String, userText: String): String
}

class ChatRepositoryImpl(
    private val jwt: String,
    private val client: HttpClient = HttpClientProvider.client
) : ChatRepository {

    private val url = "${ApiConfig.BASE_URL}/chatbot/chat"   // <- single endpoint

    /** POST /api/v1/chatbot/chat  */
    override suspend fun sendMessage(sessionId: String, userText: String): String {
        // ① build the JSON body expected by the FastAPI endpoint
        val requestBody = ChatCompletionRequestDto(
            messages = listOf(ChatRoleMessageDto(role = "user", content = userText))
        )

        // ② perform the HTTP call — the session is identified by the *JWT*
        val resp: ChatCompletionResponseDto = client.post(url) {
            header(HttpHeaders.Authorization, "Bearer $jwt")
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()

        // ③ extract the assistant’s first reply (fallback if none)
        return resp.messages.firstOrNull { it.role == "assistant" }?.content
            ?: "(no reply)"
    }
}

