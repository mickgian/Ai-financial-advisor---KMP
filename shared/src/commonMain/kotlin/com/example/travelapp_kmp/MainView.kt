package com.example.travelapp_kmp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.travelapp_kmp.models.SessionResponse
import com.example.travelapp_kmp.models.TokenResponse
import com.example.travelapp_kmp.network.auth.SessionRepositoryImpl
import com.example.travelapp_kmp.network.chat.ChatRepositoryImpl
import com.example.travelapp_kmp.screens.*
import com.example.travelapp_kmp.viewModels.*

@Composable
fun CommonView() {
    val nav = rememberNavController()

    /* ------------------------------------------------------------ */
    /*  Top-level app state                                          */
    /* ------------------------------------------------------------ */
    var token by remember { mutableStateOf<TokenResponse?>(null) }
    var session by remember { mutableStateOf<SessionResponse?>(null) }

    /* Decide which screen to start on */
    val startDestination =
        when {
            token == null   -> "Register"
            session == null -> "Session"
            else            -> "Chat"
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
                        token = newToken
                        nav.navigate("Session") { popUpTo("Login") { inclusive = true } }
                    },
                    onSwitchToRegister = { nav.navigate("Register") }
                )
            }


            /* ------------- SESSION (pick / create) ------------- */
            composable("Session") {
                val bearer = token?.accessToken
                if (bearer == null) {
                    // user is not logged-in → bounce to Login
                    nav.navigate("Login") { popUpTo("Session") { inclusive = true } }
                    return@composable
                }

                val sessionVm = remember { SessionViewModel(token = bearer) }

                SessionListScreen(
                    vm = sessionVm,
                    onPick = { chosen ->
                        session = chosen
                        token   = chosen.token
                        nav.navigate("Chat")
                    },
                    onLogout = {           // clear creds + bounce to login
                        token   = null
                        session = null
                        nav.navigate("Login") {
                            popUpTo("Session") { inclusive = true }
                        }
                    }
                )
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

                ChatScreen(
                    vm      = chatVm,
                    onBack  = {
                        nav.popBackStack()
                      },   // pop to SessionListScreen
                    onLogout = {                        // clear creds + bounce to login
                        token   = null
                        session = null
                        nav.navigate("Login") {
                            popUpTo("Chat") { inclusive = true }
                        }
                    }
                )
            }

        }
    }
}
