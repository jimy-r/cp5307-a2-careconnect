// Defines the file's location within the UI screen architecture for the admin section.
package com.example.careconnect.ui.screen.admin

// Imports necessary libraries for layout, UI components, navigation, and ViewModels.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.careconnect.ui.components.AdminButton
import com.example.careconnect.ui.navigation.AuthRoute
import com.example.careconnect.ui.navigation.MainRoute
import com.example.careconnect.ui.navigation.Screen
import com.example.careconnect.ui.viewmodel.AdminViewModel

// Defines the main UI composable for the Admin screen.
@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    // Collects the user's profile from the ViewModel as a state that recomposes on change.
    val user by viewModel.userProfile.collectAsState()

    // Arranges all UI elements vertically with centered alignment.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Displays a large, generic profile picture icon.
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = "Profile",
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        // Adds a fixed amount of vertical space.
        Spacer(modifier = Modifier.height(8.dp))
        // Displays the user's name fetched from the database, showing "Loading..." initially.
        Text(user?.name ?: "Loading...", style = MaterialTheme.typography.headlineMedium)
        // Displays the user's email address fetched from the database.
        Text(user?.email ?: "", style = MaterialTheme.typography.bodyLarge)

        // Displays a horizontal line to visually separate sections.
        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // A reusable button that navigates to the user's profile editing screen.
        AdminButton("Personal Info") { navController.navigate(Screen.Profile.route) }

        // A reusable button that navigates to the list of caregivers/contacts.
        AdminButton("Contact List") { navController.navigate(Screen.Contacts.route) }

        // An expanding spacer that pushes the "Log Out" button to the bottom of the screen.
        Spacer(modifier = Modifier.weight(1f))

        // A button styled in red to indicate a significant action like logging out.
        Button(
            onClick = {
                // Calls the ViewModel to sign the user out of Firebase.
                viewModel.signOut()
                // Navigates to the login screen and clears the entire back stack.
                navController.navigate(AuthRoute.ROOT) {
                    popUpTo(MainRoute.ROOT) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            // The text displayed inside the log out button.
            Text("Log Out")
        }
    }
}

