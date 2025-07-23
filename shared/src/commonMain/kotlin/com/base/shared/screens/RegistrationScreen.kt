package com.base.shared.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.base.shared.utils.PasswordValidator
import com.base.shared.viewModels.RegistrationViewModel

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
        
        val passwordFocusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            ),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.focusRequester(passwordFocusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    val passwordValidation = PasswordValidator.validatePassword(password)
                    if (state is RegistrationViewModel.State.Idle && 
                        email.isNotBlank() && passwordValidation.isValid) {
                        vm.register(email, password)
                    }
                }
            ),
            singleLine = true
        )
        
        // Show password requirements when user starts typing password
        if (password.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    "Password requirements:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(4.dp))
                
                PasswordValidator.getPasswordRequirements().forEach { requirement ->
                    val isMet = PasswordValidator.checkRequirement(password, requirement)
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (isMet) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = if (isMet) "Requirement met" else "Requirement not met",
                            tint = if (isMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.width(16.dp).height(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = requirement,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))

        // Always show button in same position to prevent layout shift
        when (state) {
            RegistrationViewModel.State.Loading -> {
                CircularProgressIndicator()
            }
            RegistrationViewModel.State.Success -> {
                // auto-proceed
                LaunchedEffect(Unit) { onRegistered() }
            }
            else -> {
                // Show button for Idle and Error states
                val passwordValidation = PasswordValidator.validatePassword(password)
                Button(
                    onClick = { vm.register(email, password) },
                    enabled = state !is RegistrationViewModel.State.Loading && 
                              email.isNotBlank() && passwordValidation.isValid
                ) {
                    Text("Register")
                }
            }
        }
        
        // Show specific error message below button
        if (state is RegistrationViewModel.State.Error) {
            val errorState = state as RegistrationViewModel.State.Error
            Spacer(Modifier.height(12.dp))
            Text(
                text = errorState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onSwitchToLogin) {
            Text("Already have an account? Log in")
        }
    }
}

