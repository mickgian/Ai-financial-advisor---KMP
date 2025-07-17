package com.example.travelapp_kmp.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String
)

@Serializable
data class SessionResponse(
    @SerialName("session_id") val sessionId: String,
    val name: String,
    val token: TokenResponse
)

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type")  val tokenType:  String,
    @SerialName("expires_at")  val expiresAt:  Instant
)

@Serializable data class AuthResponse(
    val id: Int,
    val email: String,
    val token: TokenResponse
)

// chat
@Serializable data class ChatRequest(
    val sessionId: String,
    val message: String
)
@Serializable data class ChatResponse(
    val text: String
)
