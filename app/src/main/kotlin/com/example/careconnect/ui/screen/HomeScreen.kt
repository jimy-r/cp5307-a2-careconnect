package com.example.careconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.ui.components.DashboardCard
import com.example.careconnect.ui.components.LoadingIndicator
import com.example.careconnect.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ✅ UPDATED: This now gets the name from the user profile in our state object.
        // It will show "Loading..." briefly, then update to the user's real name.
        Text(
            text = "Welcome, ${state.userProfile?.name ?: "Loading..."}",
            style = MaterialTheme.typography.headlineMedium
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else {
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

            state.nextMedication?.let { med ->
                DashboardCard(
                    icon = Icons.Default.Medication,
                    title = "Next Medication: ${med.name}",
                    subtitle = "Due at ${timeFormat.format(med.time)}"
                )
            } ?: DashboardCard(
                icon = Icons.Default.Medication,
                title = "No Upcoming Medications",
                subtitle = "All caught up for today"
            )

            state.upcomingAppointment?.let { appt ->
                DashboardCard(
                    icon = Icons.Default.Event,
                    title = "Next Appointment: ${appt.title}",
                    subtitle = "Today at ${timeFormat.format(appt.dateTime)}"
                )
            } ?: DashboardCard(
                icon = Icons.Default.Event,
                title = "No Upcoming Appointments",
                subtitle = "Check the schedule for details"
            )
        }
    }
}