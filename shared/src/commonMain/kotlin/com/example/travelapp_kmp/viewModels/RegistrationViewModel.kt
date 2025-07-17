package com.example.travelapp_kmp.viewModels

import com.example.travelapp_kmp.network.auth.AuthRepository
import com.example.travelapp_kmp.network.auth.AuthRepositoryImpl
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) {
    sealed interface State {
        object Idle    : State
        object Loading : State
        data class Error(val message: String) : State
        object Success : State
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    // scope for launching coroutines on the Main thread, with error handler
    private val viewModelScope = CoroutineScope(
        Dispatchers.Main + SupervisorJob() +
                CoroutineExceptionHandler { _, e ->
                    _state.value = State.Error(e.message ?: "Unknown error")
                }
    )

    /**
     * Kick off the registration network call.
     *
     * @param email the user's email
     * @param password the chosen password
     */
    fun register(username: String, email: String) {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                // call our Ktor-based repository
                repository.register(username, email)
                _state.value = State.Success
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "Registration failed")
            }
        }
    }
}
