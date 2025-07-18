package com.example.travelapp_kmp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp_kmp.models.ChatMessage
import com.example.travelapp_kmp.models.ChatUiState
import com.example.travelapp_kmp.models.SessionResponse
import com.example.travelapp_kmp.network.auth.SessionRepository
import com.example.travelapp_kmp.network.chat.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repo: ChatRepository,          // ↖ talk to /chatbot/chat
    private val sessionRepo: SessionRepository, // ↖ should you need CRUD ops
    private val initialSession: SessionResponse // ↖ gives us sessionId & token
) : ViewModel() {

    private val _state = MutableStateFlow<ChatUiState>(
        ChatUiState.History(emptyList())
    )
    val state: StateFlow<ChatUiState> = _state

    /** Local rolling buffer rendered by the UI */
    private val buffer = mutableListOf<ChatMessage>()

    /** Always valid: taken from `initialSession` or a freshly-created one */
    private var sessionId: String = initialSession.sessionId

    /** Send a user message → optimistic UI → backend → assistant reply   */
    fun sendMessage(userText: String) = viewModelScope.launch {
        // optimistic append
        buffer += ChatMessage(text = userText, fromUser = true)
        _state.value = ChatUiState.History(buffer.toList())

        // remote call
        _state.value = ChatUiState.Loading
        runCatching { repo.sendMessage(sessionId, userText) }
            .onSuccess { assistantText ->
                buffer += ChatMessage(text = assistantText, fromUser = false)
                _state.value = ChatUiState.History(buffer.toList())
            }
            .onFailure { e ->
                _state.value = ChatUiState.Error(e.message ?: "Send failed")
            }
    }

    /** Optional helper: start an **empty** brand-new session */
    suspend fun startNewSession() {
        _state.value = ChatUiState.Loading
        val new = sessionRepo.createSession()
        sessionId = new.sessionId
        buffer.clear()
        _state.value = ChatUiState.History(emptyList())
    }
}
