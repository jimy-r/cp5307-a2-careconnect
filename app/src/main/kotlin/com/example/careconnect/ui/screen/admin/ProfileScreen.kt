// Defines the file's location within the UI screen architecture for the admin section.
package com.example.careconnect.ui.screen.admin

// Imports necessary libraries for UI components, layout, navigation, and ViewModels.
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.careconnect.ui.navigation.AuthRoute
import com.example.careconnect.ui.navigation.MainRoute
import com.example.careconnect.ui.viewmodel.ProfileViewModel

// Opts in to using experimental APIs from Material Design 3.
@OptIn(ExperimentalMaterial3Api::class)
// Defines the main UI composable for the user's profile editing screen.
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // Collects the screen's state from the ViewModel, rebuilding the UI on changes.
    val state by viewModel.uiState.collectAsState()

    // A side-effect that navigates back automatically after a successful save.
    LaunchedEffect(state.isSaveSuccess) {
        if (state.isSaveSuccess) {
            navController.popBackStack()
        }
    }
    // A side-effect that navigates to the login screen after a successful deletion.
    LaunchedEffect(state.isDeleteSuccess) {
        if (state.isDeleteSuccess) {
            navController.navigate(AuthRoute.ROOT) {
                popUpTo(MainRoute.ROOT) { inclusive = true }
            }
        }
    }

    // Conditionally displays the delete confirmation dialog based on the ViewModel's state.
    if (state.isConfirmDeleteDialogOpen) {
        DeleteConfirmDialog(
            onDismiss = viewModel::hideDeleteDialog,
            onConfirm = viewModel::deleteProfile
        )
    }

    // Sets up the main screen structure with a top app bar.
    Scaffold(
        topBar = {
            // Defines the top app bar with a title and a back navigation button.
            TopAppBar(
                title = { Text("Edit Personal Info") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        // Arranges the input fields and buttons vertically.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // A text field for editing the user's full name.
            OutlinedTextField(value = state.currentName, onValueChange = viewModel::onNameChange, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            // A text field for editing the user's phone number.
            OutlinedTextField(value = state.currentPhone, onValueChange = viewModel::onPhoneChange, label = { Text("Phone Contact") }, modifier = Modifier.fillMaxWidth())
            // A text field for editing the user's address.
            OutlinedTextField(value = state.currentAddress, onValueChange = viewModel::onAddressChange, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())

            // The custom dropdown composable for selecting the user's role.
            RoleDropdown(
                selectedRole = state.currentRole,
                onRoleSelected = viewModel::onRoleChange
            )

            // An expanding spacer that pushes the buttons to the bottom.
            Spacer(modifier = Modifier.weight(1f))

            // The primary button to save all changes made on the screen.
            Button(onClick = viewModel::saveProfile, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth()) {
                Text("Save Changes")
            }

            // A secondary, destructively-styled button that opens the delete confirmation dialog.
            TextButton(
                onClick = viewModel::showDeleteDialog,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Delete Profile")
            }
        }
    }
}

// Opts in to using experimental APIs from Material Design 3.
@OptIn(ExperimentalMaterial3Api::class)
// Defines a custom, reusable dropdown menu that looks like a text field.
@Composable
fun RoleDropdown(selectedRole: String, onRoleSelected: (String) -> Unit) {
    // State to manage whether the dropdown menu is currently open or closed.
    var expanded by remember { mutableStateOf(false) }
    // A predefined list of the available roles for the user to select.
    val roles = listOf("Primary Caregiver", "Secondary Caregiver", "Patient")

    // A Box layout to layer the clickable area and dropdown menu over the visual text field.
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // A disabled text field used only for its visual appearance.
        OutlinedTextField(
            value = selectedRole,
            onValueChange = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Carer Role") },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown arrow") },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        // A transparent, clickable surface layered on top to trigger the dropdown.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = { expanded = true })
        )

        // The actual dropdown menu that appears when 'expanded' is true.
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Creates a clickable menu item for each role in the predefined list.
            roles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role) },
                    onClick = {
                        onRoleSelected(role)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Defines a reusable confirmation dialog for the delete profile action.
@Composable
fun DeleteConfirmDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    // The main dialog container that handles dismissal requests.
    AlertDialog(
        onDismissRequest = onDismiss,
        // The title text of the confirmation dialog.
        title = { Text("Delete Profile?") },
        // The descriptive body text of the confirmation dialog.
        text = { Text("Are you sure you want to permanently delete your profile? This action cannot be undone.") },
        // The confirmation button, styled in red to indicate a destructive action.
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        // The dismissal (cancel) button for the dialog.
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

