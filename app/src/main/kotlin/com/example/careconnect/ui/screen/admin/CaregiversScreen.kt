// Defines the file's location within the UI screen architecture for the admin section.
package com.example.careconnect.ui.screen.admin

// Imports necessary libraries for layout, UI components, navigation, and ViewModels.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.careconnect.data.model.User
import com.example.careconnect.ui.components.LoadingIndicator
import com.example.careconnect.ui.viewmodel.CaregiversViewModel

// Opts in to using experimental APIs from Material Design 3.
@OptIn(ExperimentalMaterial3Api::class)
// Defines the main UI composable for the Caregivers/Contact List screen.
@Composable
fun CaregiversScreen(
    navController: NavController,
    viewModel: CaregiversViewModel = hiltViewModel()
) {
    // Collects the screen's state from the ViewModel, rebuilding the UI on changes.
    val state by viewModel.uiState.collectAsState()

    // Sets up the main screen structure with a top app bar.
    Scaffold(
        topBar = {
            // Defines the top app bar with a title and a back navigation button.
            TopAppBar(
                title = { Text("Contact List") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        // Shows a loading indicator if the data is currently being fetched.
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else {
            // Displays a scrollable list of caregiver items once data is loaded.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Iterates through the list of caregivers and creates a list item for each one.
                items(state.caregivers, key = { it.uid }) { user ->
                    CaregiverListItem(user = user)
                }
            }
        }
    }
}

// Defines a reusable UI component for displaying a single caregiver's information.
@Composable
fun CaregiverListItem(user: User) {
    // Creates a card to contain the contact's details.
    Card(modifier = Modifier.fillMaxWidth()) {
        // Arranges the icon and text details horizontally.
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Displays a generic person icon for the contact.
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            // Adds space between the icon and the text.
            Spacer(modifier = Modifier.width(16.dp))
            // Arranges the textual information vertically.
            Column(modifier = Modifier.weight(1f)) {
                // Displays the caregiver's name.
                Text(user.name, style = MaterialTheme.typography.titleMedium)
                // Displays the caregiver's assigned role.
                Text(
                    user.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Displays the caregiver's phone number if it exists.
                if (user.phone.isNotBlank()) {
                    Text("Phone: ${user.phone}", style = MaterialTheme.typography.bodySmall)
                }
                // Displays the caregiver's address if it exists.
                if (user.address.isNotBlank()) {
                    Text("Address: ${user.address}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// Defines an unused but available dialog for changing a user's role in the future.
@Composable
fun ChangeRoleDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: (userId: String, newRole: String) -> Unit
) {
    // Implementation for the dialog UI and logic would go here.
}

