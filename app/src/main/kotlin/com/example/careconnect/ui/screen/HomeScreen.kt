// Defines the file's location within the UI screen architecture.
package com.example.careconnect.ui.screen

// Imports necessary libraries for layout, UI components, state management, and ViewModels.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.ui.components.DashboardCard
import com.example.careconnect.ui.components.LoadingIndicator
import com.example.careconnect.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

// Defines the main UI composable for the Home/Dashboard screen.
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    // Collects the screen's state from the ViewModel, rebuilding the UI on changes.
    val state by viewModel.uiState.collectAsState()

    // Arranges all the dashboard cards vertically with spacing between them.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Displays a personalized welcome message with the user's name.
        Text(
            text = "Welcome, ${state.userProfile?.name ?: "Loading..."}",
            style = MaterialTheme.typography.headlineMedium
        )

        // Conditionally displays a loading spinner while data is being fetched.
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
            // Displays the main content cards once the data has finished loading.
        } else {
            // Displays the next medication card if one exists, otherwise shows a default message.
            state.nextMedication?.let { med ->
                DashboardCard(
                    icon = Icons.Default.Medication,
                    title = "Next Medication: ${med.name}",
                    subtitle = "Due at ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(med.time)}"
                )
            } ?: DashboardCard(
                icon = Icons.Default.Medication,
                title = "No Upcoming Medications",
                subtitle = "All caught up for today"
            )

            // Displays the next appointment card if one exists, otherwise shows a default message.
            state.upcomingAppointment?.let { appt ->
                DashboardCard(
                    icon = Icons.Default.Event,
                    title = "Next Appointment: ${appt.title}",
                    subtitle = "Today at ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(appt.dateTime)}"
                )
            } ?: DashboardCard(
                icon = Icons.Default.Event,
                title = "No Upcoming Appointments",
                subtitle = "Check the schedule for details"
            )

            // Displays the latest journal entry if one exists, otherwise shows a default message.
            state.latestJournalEntry?.let { entry ->
                DashboardCard(
                    icon = Icons.AutoMirrored.Filled.Notes,
                    title = "Latest Journal Update",
                    subtitle = "${entry.authorName}: \"${entry.note}\"",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            } ?: DashboardCard(
                icon = Icons.AutoMirrored.Filled.Notes,
                title = "No Journal Entries Yet",
                subtitle = "Add an entry in the Journal tab"
            )
        }
    }
}