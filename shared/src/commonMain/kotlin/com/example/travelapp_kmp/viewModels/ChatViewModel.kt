package com.example.travelapp_kmp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp_kmp.models.*
import com.example.travelapp_kmp.network.auth.SessionRepository
import com.example.travelapp_kmp.network.chat.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repo: ChatRepository,
    private val sessionRepo: SessionRepository,
    private val initialSession: SessionResponse
) : ViewModel() {

    private val _state = MutableStateFlow<ChatUiState>(ChatUiState.History(emptyList()))
    val state: StateFlow<ChatUiState> = _state

    private val messages = mutableListOf<ChatMessage>()
    private var sessionId: String = initialSession.sessionId   // never null

    fun sendMessage(text: String) = viewModelScope.launch {
        // optimistic append
        messages += ChatMessage(text = text, fromUser = true)
        _state.value = ChatUiState.History(messages.toList())

        _state.value = ChatUiState.Loading
        runCatching { repo.sendMessage(sessionId, text) }
            .onSuccess { resp ->
                messages += ChatMessage(text = resp.text, fromUser = false)
                _state.value = ChatUiState.History(messages.toList())
            }
            .onFailure { e ->
                _state.value = ChatUiState.Error(e.message ?: "Send failed")
            }
    }

    /** optional helper if we ever need a brand-new session */
    suspend fun startNewSession() {
        _state.value = ChatUiState.Loading
        sessionId = sessionRepo.createSession().sessionId
        messages.clear()
        _state.value = ChatUiState.History(emptyList())
    }
}
