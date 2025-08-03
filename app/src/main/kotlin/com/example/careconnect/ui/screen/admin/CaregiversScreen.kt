package com.example.careconnect.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.careconnect.data.model.User
import com.example.careconnect.ui.viewmodel.CaregiversViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiversScreen(
    navController: NavController,
    viewModel: CaregiversViewModel = hiltViewModel()
) {
    val caregivers by viewModel.caregivers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Care Circle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Show invite dialog */ }) {
                Icon(Icons.Default.Add, contentDescription = "Invite Caregiver")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(caregivers) { caregiver ->
                CaregiverListItem(caregiver = caregiver)
            }
        }
    }
}

@Composable
fun CaregiverListItem(caregiver: User) {
    Card(modifier = Modifier.fillMaxWidth()) {
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
                Text(caregiver.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    caregiver.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { /* TODO: Show options menu */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }
        }
    }
}

