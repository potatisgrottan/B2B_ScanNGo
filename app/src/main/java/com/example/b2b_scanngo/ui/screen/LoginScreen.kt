package com.example.b2b_scanngo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2b_scanngo.viewModel.MainViewModel

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "B2B Scan-N-Go", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-post") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Lösenord") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        viewModel.authError?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            // LOGGA IN KNAPP
// Inuti LoginScreen-kolumnen
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        viewModel.signIn(email, password) {
                            isLoading = false
                            onLoginSuccess()
                        }
                    } else {
                        // Sätt ett lokalt felmeddelande eller använd ViewModels authError
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("LOGGA IN")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        viewModel.signUp(email, password) {
                            isLoading = false
                            onLoginSuccess()
                        }
                    } else {
                        // Om fälten är tomma, stanna direkt här
                        isLoading = false
                    }
                }
            ) {
                Text("Inget konto? Registrera dig här")
            }
        }
    }
}