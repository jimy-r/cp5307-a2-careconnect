// Defines the file's location within the UI screen architecture for authentication.
package com.example.careconnect.ui.screen.auth

// Imports necessary libraries for UI components, layout, state management, and ViewModels.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.careconnect.ui.components.ErrorCard
import com.example.careconnect.ui.components.LoadingIndicator
import com.example.careconnect.ui.viewmodel.AuthViewModel

// Defines the main UI composable for the Login screen.
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // A state variable to hold the text currently in the email input field.
    var email by remember { mutableStateOf("") }
    // A state variable to hold the text currently in the password input field.
    var password by remember { mutableStateOf("") }
    // Retrieves the current authentication state (loading, error) from the ViewModel.
    val authState = viewModel.authState

    // A side-effect that can be used to handle UI changes when an error occurs.
    LaunchedEffect(authState.error) {
        if (authState.error != null) {
            // This block runs whenever a new error message is set in the ViewModel.
        }
    }

    // A layout composable that centers its content both vertically and horizontally.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Arranges all the UI elements vertically with spacing between them.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Displays the main title text of the screen.
            Text("Welcome to CareConnect", style = MaterialTheme.typography.headlineMedium)

            // The input text field for the user's email address.
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // The input text field for the user's password, which obscures the text.
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // Conditionally shows either a loading spinner or the login button.
            if (authState.isLoading) {
                LoadingIndicator()
            } else {
                // The primary button that triggers the login action in the ViewModel.
                Button(
                    onClick = { viewModel.login(email, password, onLoginSuccess) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
            }

            // A secondary button that navigates the user to the registration screen.
            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register")
            }

            // Conditionally displays an error card if an error message exists in the state.
            authState.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorCard(errorMessage = it)
            }
        }
    }
}

