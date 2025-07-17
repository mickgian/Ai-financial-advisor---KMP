package com.example.travelapp_kmp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.travelapp_kmp.models.TokenResponse
import com.example.travelapp_kmp.viewModels.LoginViewModel

@Composable
fun LoginScreen(
    vm: LoginViewModel = remember { LoginViewModel() },
    onSuccess: (TokenResponse) -> Unit,
    onSwitchToRegister: () -> Unit
) {
    val state by vm.loginState.collectAsState()

    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        when (state) {
            LoginViewModel.LoginState.Idle,
            is LoginViewModel.LoginState.Error -> {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    /* --- fields --- */
                    OutlinedTextField(user, { user = it },  label = { Text("Username") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        pass, { pass = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(16.dp))

                    /* --- login button --- */
                    Button(onClick = { vm.login(user, pass) }) { Text("Login") }

                    if (state is LoginViewModel.LoginState.Error) {
                        Text(
                            (state as LoginViewModel.LoginState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    /* --- switch to register --- */
                    TextButton(
                        onClick = onSwitchToRegister,
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        Text("Don't have an account? Register")
                    }
                }
            }

            LoginViewModel.LoginState.Loading -> CircularProgressIndicator()

            is LoginViewModel.LoginState.Success -> {
                val token = (state as LoginViewModel.LoginState.Success).token            // TokenResponse
                onSuccess(token)
            }


        }
    }
}
