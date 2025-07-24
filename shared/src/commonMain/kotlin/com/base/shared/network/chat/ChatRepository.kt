package com.base.shared.network.chat

import com.base.shared.ApiConfig
import com.base.shared.network.HttpClientProvider
import com.base.shared.utils.NetworkLogger
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

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
        val requestBody = ChatCompletionRequestDto(
            messages = listOf(ChatRoleMessageDto(role = "user", content = userText))
        )

        // Log the request details
        NetworkLogger.logRequest(
            method = "POST",
            url = url,
            headers = mapOf("Authorization" to "Bearer ${jwt.take(10)}..."), 
            body = Json.encodeToString(ChatCompletionRequestDto.serializer(), requestBody),
            tag = "ChatRepo"
        )

        try {
            val httpResponse: HttpResponse = client.post(url) {
                header(HttpHeaders.Authorization, "Bearer $jwt")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            // Get response body once
            val responseBody = httpResponse.bodyAsText()
            
            // Log successful response
            NetworkLogger.logResponse(
                method = "POST",
                url = url,
                statusCode = httpResponse.status.value,
                body = responseBody,
                tag = "ChatRepo"
            )

            // Parse the response
            val resp: ChatCompletionResponseDto = Json.decodeFromString(ChatCompletionResponseDto.serializer(), responseBody)
            return resp.messages.firstOrNull { it.role == "assistant" }?.content ?: "(no reply)"
        } catch (e: Exception) {
            NetworkLogger.logError(
                method = "POST",
                url = url,
                error = e.message ?: "Unknown error",
                tag = "ChatRepo"
            )
            throw e
        }
    }
}

