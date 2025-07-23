package com.base.shared.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.shared.models.ChatMessage
import com.base.shared.models.ChatUiState
import com.base.shared.viewModels.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    vm: ChatViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {

    val uiState by vm.state.collectAsState()
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            // in ChatScreen top bar
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                title = { Text("Chat") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message") }
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    enabled = input.isNotBlank(),
                    onClick = {
                        scope.launch {
                            vm.sendMessage(input.trim())
                            input = ""
                        }
                    }
                ) {
                    Text("Send", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->

        when (uiState) {
            ChatUiState.Loading ->
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    CircularProgressIndicator()
                }

            is ChatUiState.Error ->
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    Text((uiState as ChatUiState.Error).message)
                }

            is ChatUiState.History -> {
                val history = (uiState as ChatUiState.History).messages
                LazyColumn(
                    contentPadding = padding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(history) { msg ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement =
                                if (msg.fromUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = if (msg.fromUser)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    msg.text,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .widthIn(max = 280.dp)
                                )
                            }
                        }
                    }
                }
            }

            ChatUiState.Idle -> { /* empty */ }
        }
    }
}

// Preview Composables for development and testing

/**
 * Preview function that demonstrates the ChatScreen with different UI states.
 * 
 * For Android: Use @Preview annotation in androidApp module
 * For Desktop: Create preview window with this function
 * For Web/iOS: Use in development builds for testing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContentPreview(
    state: ChatUiState = ChatUiState.Idle,
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var input by remember { mutableStateOf("Sample message") }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                title = { Text("Chat") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message") }
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    enabled = input.isNotBlank(),
                    onClick = { /* No-op for preview */ }
                ) {
                    Text("Send", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        when (state) {
            ChatUiState.Loading ->
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    CircularProgressIndicator()
                }

            is ChatUiState.Error ->
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    Text(state.message)
                }

            is ChatUiState.History -> {
                LazyColumn(
                    contentPadding = padding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.messages) { msg ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement =
                                if (msg.fromUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = if (msg.fromUser)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    msg.text,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .widthIn(max = 280.dp)
                                )
                            }
                        }
                    }
                }
            }

            ChatUiState.Idle -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Start a conversation",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

// Specific preview states for easy testing
object ChatScreenPreviews {
    @Composable
    fun Idle() = ChatScreenContentPreview(ChatUiState.Idle)
    
    @Composable
    fun Loading() = ChatScreenContentPreview(ChatUiState.Loading)
    
    @Composable
    fun Error() = ChatScreenContentPreview(
        ChatUiState.Error("Network connection failed. Please try again.")
    )
    
    @Composable
    fun WithMessages() {
        val sampleMessages = listOf(
            ChatMessage(text = "Hello! How can I help you today?", fromUser = false),
            ChatMessage(text = "I need help with my project setup", fromUser = true),
            ChatMessage(text = "I'd be happy to help! What specific part of the setup are you having trouble with?", fromUser = false),
            ChatMessage(text = "I'm trying to configure the database connection", fromUser = true),
            ChatMessage(text = "Great! Let me guide you through the database configuration process...", fromUser = false)
        )
        ChatScreenContentPreview(ChatUiState.History(sampleMessages))
    }
}
