package com.example.careconnect.ui.screen.admin

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Handle navigation after save or delete
    LaunchedEffect(state.isSaveSuccess) {
        if (state.isSaveSuccess) {
            navController.popBackStack()
        }
    }
    LaunchedEffect(state.isDeleteSuccess) {
        if (state.isDeleteSuccess) {
            navController.navigate(AuthRoute.ROOT) {
                popUpTo(MainRoute.ROOT) { inclusive = true }
            }
        }
    }

    // Show confirmation dialog if needed
    if (state.isConfirmDeleteDialogOpen) {
        DeleteConfirmDialog(
            onDismiss = viewModel::hideDeleteDialog,
            onConfirm = viewModel::deleteProfile
        )
    }

    Scaffold(
        topBar = {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = state.currentName, onValueChange = viewModel::onNameChange, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = state.currentPhone, onValueChange = viewModel::onPhoneChange, label = { Text("Phone Contact") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = state.currentAddress, onValueChange = viewModel::onAddressChange, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())

            RoleDropdown(
                selectedRole = state.currentRole,
                onRoleSelected = viewModel::onRoleChange
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = viewModel::saveProfile, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth()) {
                Text("Save Changes")
            }

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

// ✅ THIS ENTIRE COMPOSABLE HAS BEEN REWRITTEN TO FIX THE CLICK ISSUE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDropdown(selectedRole: String, onRoleSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("Primary Caregiver", "Secondary Caregiver", "Patient")

    // The whole element is a Box that handles the click to expand the menu.
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // This is a "fake" TextField. It's disabled so it doesn't consume clicks,
        // but we override its colors so it doesn't look grayed out.
        OutlinedTextField(
            value = selectedRole,
            onValueChange = {},
            enabled = false, // <-- Critically, this is false
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

        // This is a transparent box layered on top that catches the click.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = { expanded = true })
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
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

@Composable
fun DeleteConfirmDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Profile?") },
        text = { Text("Are you sure you want to permanently delete your profile? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

