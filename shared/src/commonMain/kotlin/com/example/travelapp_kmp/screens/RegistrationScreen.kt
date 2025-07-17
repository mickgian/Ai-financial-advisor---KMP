package com.example.travelapp_kmp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.travelapp_kmp.viewModels.RegistrationViewModel

@Composable
fun RegistrationScreen(
    vm: RegistrationViewModel = remember { RegistrationViewModel() },
    onRegistered: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))

        when (state) {
            RegistrationViewModel.State.Idle -> {
                Button(onClick = {
                    vm.register( email, password)
                }) {
                    Text("Register")
                }
            }
            RegistrationViewModel.State.Loading -> {
                CircularProgressIndicator()
            }
            is RegistrationViewModel.State.Error -> {
                Text(
                    (state as RegistrationViewModel.State.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            RegistrationViewModel.State.Success -> {
                // auto-proceed
                LaunchedEffect(Unit) { onRegistered() }
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onSwitchToLogin) {
            Text("Already have an account? Log in")
        }
    }
}

