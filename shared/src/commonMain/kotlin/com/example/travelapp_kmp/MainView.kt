// shared/src/commonMain/kotlin/MainView.kt
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
                if (bearer == null) {                     // safety net
                    nav.navigate("Login") { popUpTo("Session") { inclusive = true } }
                    return@composable
                }

                val sessionVm = remember {
                    SessionViewModel(token = bearer)
                }

                /** Option A – auto-create and jump straight to chat */
                LaunchedEffect(Unit) {
                    session = sessionVm.createAndUseNewSession()
                    nav.navigate("Chat") {
                        popUpTo("Session") { inclusive = true }
                    }
                }

                /** Option B – let user choose/rename sessions */
                /* SessionListScreen(
                    vm = sessionVm,
                    onPick = { chosen ->
                        session = chosen
                        nav.navigate("Chat") {
                            popUpTo("Session") { inclusive = true }
                        }
                    }
                 ) */
            }

            /* ---------------- CHAT -------------------- */
            composable("Chat") {
                val bearer = token?.accessToken
                val chatSession = session

                if (bearer == null || chatSession == null) {
                    // Something is missing – fall back to login
                    LaunchedEffect(Unit) {
                        nav.navigate("Login") { popUpTo("Chat") { inclusive = true } }
                    }
                    return@composable
                }

                val chatVm = remember {
                    ChatViewModel(
                        repo = ChatRepositoryImpl(bearer),
                        sessionRepo = SessionRepositoryImpl(bearer),
                        initialSession = chatSession
                    )
                }

                ChatScreen(
                    vm = chatVm,
                    onBack = {
                        // user wants to sign out
                        token = null
                        session = null
                        nav.navigate("Login") { popUpTo("Chat") { inclusive = true } }
                    }
                )
            }
        }
    }
}
