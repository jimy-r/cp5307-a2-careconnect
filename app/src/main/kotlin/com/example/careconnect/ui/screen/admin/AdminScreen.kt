package com.example.careconnect.ui.screen.admin

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

@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val user by viewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = "Profile",
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(user?.name ?: "Loading...", style = MaterialTheme.typography.headlineMedium)
        Text(user?.email ?: "", style = MaterialTheme.typography.bodyLarge)

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        AdminButton("Personal Info") { navController.navigate(Screen.Profile.route) }

        AdminButton("Contact List") { navController.navigate(Screen.Contacts.route) }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.signOut()
                // Navigate back to login screen, clearing the history
                navController.navigate(AuthRoute.ROOT) {
                    popUpTo(MainRoute.ROOT) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Log Out")
        }
    }
}

