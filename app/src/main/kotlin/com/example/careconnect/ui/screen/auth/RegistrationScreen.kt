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

// Defines the main UI composable for the user Registration screen.
@Composable
fun RegistrationScreen(
    viewModel: AuthViewModel,
    onRegistrationSuccess: () -> Unit
) {
    // A state variable to hold the text currently in the name input field.
    var name by remember { mutableStateOf("") }
    // A state variable to hold the text currently in the email input field.
    var email by remember { mutableStateOf("") }
    // A state variable to hold the text currently in the password input field.
    var password by remember { mutableStateOf("") }
    // Retrieves the current authentication state (loading, error) from the ViewModel.
    val authState = viewModel.authState

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
            Text("Create Account", style = MaterialTheme.typography.headlineMedium)

            // The input text field for the user's full name.
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

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

            // Conditionally shows either a loading spinner or the register button.
            if (authState.isLoading) {
                LoadingIndicator()
            } else {
                // The primary button that triggers the registration action in the ViewModel.
                Button(
                    onClick = { viewModel.register(name, email, password, onRegistrationSuccess) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register")
                }
            }

            // Conditionally displays an error card if an error message exists in the state.
            authState.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorCard(errorMessage = it)
            }
        }
    }
}