package com.example.careconnect.ui.screen.admin

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiversScreen(
    navController: NavController,
    viewModel: CaregiversViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // ✅ REMOVED: The ChangeRoleDialog is no longer called from this screen.
    // if (state.isRoleDialogVisible && state.userToEdit != null) { ... }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact List") }, // Updated title for clarity
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.caregivers, key = { it.uid }) { user ->
                    CaregiverListItem(user = user) // Removed the onClick handler
                }
            }
        }
    }
}

@Composable
fun CaregiverListItem(user: User) { // ✅ REMOVED: The onClick parameter is gone.
    Card(modifier = Modifier.fillMaxWidth()) { // ✅ REMOVED: The .clickable modifier is gone.
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    user.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (user.phone.isNotBlank()) {
                    Text("Phone: ${user.phone}", style = MaterialTheme.typography.bodySmall)
                }
                if (user.address.isNotBlank()) {
                    Text("Address: ${user.address}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// The ChangeRoleDialog composable can be left in the file for future use,
// or deleted if you are sure you won't need it. It is no longer being used.
@Composable
fun ChangeRoleDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: (userId: String, newRole: String) -> Unit
) {
    // ... (implementation remains the same, but is now unused on this screen)
}

