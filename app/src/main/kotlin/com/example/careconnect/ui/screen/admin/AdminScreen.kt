package com.example.careconnect.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.careconnect.ui.components.AdminButton
import com.example.careconnect.ui.navigation.Screen
import com.example.careconnect.ui.viewmodel.HomeViewModel

@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user = viewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(user?.displayName ?: "User", style = MaterialTheme.typography.headlineMedium)
        Text(user?.email ?: "", style = MaterialTheme.typography.bodyLarge)

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        AdminButton("Personal Info") { /* TODO */ }
        AdminButton("Caregivers") { navController.navigate(Screen.Caregivers.route) }
        AdminButton("Accessibility") { /* TODO */ }
        AdminButton("Settings") { /* TODO */ }
    }
}

