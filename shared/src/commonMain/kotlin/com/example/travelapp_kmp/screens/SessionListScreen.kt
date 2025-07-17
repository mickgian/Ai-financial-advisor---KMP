package com.example.travelapp_kmp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.travelapp_kmp.models.SessionResponse
import com.example.travelapp_kmp.viewModels.SessionViewModel

@Composable
fun SessionListScreen(
    vm: SessionViewModel,
    onPick: (SessionResponse) -> Unit
) {
    val state by vm.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            SessionViewModel.State.Loading -> {
                CircularProgressIndicator()
            }
            is SessionViewModel.State.Error -> {
                Text(
                    text = (state as SessionViewModel.State.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is SessionViewModel.State.Ready -> {
                val sessions = (state as SessionViewModel.State.Ready).sessions

                if (sessions.isEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No sessions found")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            // you might expose a create() on our VM instead
                            // vm.createAndUseNewSession().also { onPick(it) }
                        }) {
                            Text("Create first session")
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sessions, key = { it.sessionId }) { session ->
                            Surface(
                                tonalElevation = 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPick(session) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = session.name.ifBlank { "Session ${session.sessionId}" },
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "ID: ${session.sessionId}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Button(onClick = { onPick(session) }) {
                                        Text("Use")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
