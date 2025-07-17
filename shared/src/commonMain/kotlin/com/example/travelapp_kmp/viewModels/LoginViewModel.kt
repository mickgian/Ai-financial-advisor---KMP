package com.example.travelapp_kmp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp_kmp.models.TokenResponse
import com.example.travelapp_kmp.network.auth.AuthRepository
import com.example.travelapp_kmp.network.auth.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    /** UI-facing state */
    sealed interface LoginState {
        object Idle : LoginState
        object Loading : LoginState
        data class Success(val token: TokenResponse) : LoginState
        data class Error(val message: String) : LoginState
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    /** Triggered when the user presses “Log in” */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            runCatching { repo.login(username, password) }
                .onSuccess { _loginState.value = LoginState.Success(it) }
                .onFailure { _loginState.value = LoginState.Error(it.message ?: "Login failed") }
        }
    }
}
