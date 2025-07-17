package com.example.travelapp_kmp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.travelapp_kmp.models.ChatMessage
import com.example.travelapp_kmp.models.ChatUiState
import com.example.travelapp_kmp.viewModels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    vm: ChatViewModel,
    onBack: () -> Unit
) {
    val uiState by vm.state.collectAsState()

    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI-Assistant") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message…") }
                )
                IconButton(
                    enabled = input.isNotBlank(),
                    onClick = {
                        vm.sendMessage(input.trim())
                        input = ""
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    ) { innerPadding ->
        when (uiState) {
            ChatUiState.Idle -> {}
            ChatUiState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
            is ChatUiState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text((uiState as ChatUiState.Error).message, color = MaterialTheme.colorScheme.error)
                }
            }

            is ChatUiState.History -> {
                val msgs = (uiState as ChatUiState.History).messages
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    reverseLayout = true
                ) {
                    items(msgs.reversed()) { msg ->
                        ChatBubble(msg)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage) {
    val bg = if (msg.fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val align = if (msg.fromUser) Alignment.End else Alignment.Start
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (msg.fromUser) Arrangement.End else Arrangement.Start) {
        Card(
            colors = CardDefaults.cardColors(containerColor = bg),
            modifier = Modifier.widthIn(max = 260.dp).padding(4.dp)
        ) {
            Text(msg.text, Modifier.padding(8.dp), color = if (msg.fromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
