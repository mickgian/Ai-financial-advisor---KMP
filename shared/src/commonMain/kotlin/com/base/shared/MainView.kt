package com.base.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.base.shared.auth.AuthModule
import com.base.shared.auth.AuthState
import com.base.shared.models.SessionResponse
import com.base.shared.network.auth.SessionRepositoryImpl
import com.base.shared.network.chat.ChatRepositoryImpl
import com.base.shared.screens.*
import com.base.shared.viewModels.*

@Composable
fun CommonView() {
    val nav = rememberNavController()

    /* ------------------------------------------------------------ */
    /*  Top-level app state                                          */
    /* ------------------------------------------------------------ */
    val authState by AuthModule.getAuthManager().authState.collectAsState()
    var session by remember { mutableStateOf<SessionResponse?>(null) }

    /* Decide which screen to start on */
    val startDestination = when (authState) {
        is AuthState.Loading -> "Login" // Show login while loading
        is AuthState.Unauthenticated -> "Login"
        is AuthState.Authenticated -> if (session == null) "Session" else "Chat"
        is AuthState.Error -> "Login"
    }

    MaterialTheme {
        NavHost(
            navController = nav,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {

            /* ---------------- REGISTER ---------------- */
            composable("Register") {
                RegistrationScreen(
                    vm = remember { RegistrationViewModel() },
                    onRegistered = {
                        nav.navigate("Login") {
                            popUpTo("Register") { inclusive = true }
                        }
                    },
                    onSwitchToLogin = { nav.navigate("Login") }
                )
            }

            /* ---------------- LOGIN ---------------- */
            composable("Login") {
                LoginScreen(
                    onSuccess = { newToken ->
                        nav.navigate("Session") { popUpTo("Login") { inclusive = true } }
                    },
                    onSwitchToRegister = { nav.navigate("Register") }
                )
            }


            /* ------------- SESSION (pick / create) ------------- */
            composable("Session") {
                when (val currentAuthState = authState) {
                    is AuthState.Authenticated -> {
                        val bearer = currentAuthState.token.accessToken
                        val sessionVm = remember { SessionViewModel(token = bearer) }

                        val coroutineScope = rememberCoroutineScope()
                        
                        SessionListScreen(
                            vm = sessionVm,
                            onPick = { chosen ->
                                session = chosen
                                nav.navigate("Chat")
                            },
                            onLogout = {           // clear creds + bounce to login
                                session = null
                                coroutineScope.launch {
                                    AuthModule.getAuthManager().logout()
                                }
                                nav.navigate("Login") {
                                    popUpTo("Session") { inclusive = true }
                                }
                            }
                        )
                    }
                    else -> {
                        // User is not authenticated, bounce to Login
                        LaunchedEffect(Unit) {
                            nav.navigate("Login") { popUpTo("Session") { inclusive = true } }
                        }
                    }
                }
            }

            /* ---------------- CHAT -------------------- */
            composable("Chat") {
                val chatSession = session              // SessionResponse chosen in SessionListScreen
                val bearer = chatSession?.token?.accessToken

                if (bearer == null || chatSession == null) {
                    LaunchedEffect(Unit) {
                        nav.navigate("Login") { popUpTo("Chat") { inclusive = true } }
                    }
                    return@composable
                }

                val chatVm = remember {
                    ChatViewModel(
                        repo = ChatRepositoryImpl(bearer),          // <-- session token
                        sessionRepo = SessionRepositoryImpl(bearer),       // <-- session token
                        initialSession = chatSession
                    )
                }

                val coroutineScope = rememberCoroutineScope()

                ChatScreen(
                    vm      = chatVm,
                    onBack  = {
                        nav.popBackStack()
                      },   // pop to SessionListScreen
                    onLogout = {                        // clear creds + bounce to login
                        session = null
                        coroutineScope.launch {
                            AuthModule.getAuthManager().logout()
                        }
                        nav.navigate("Login") {
                            popUpTo("Chat") { inclusive = true }
                        }
                    }
                )
            }

        }
    }
}
