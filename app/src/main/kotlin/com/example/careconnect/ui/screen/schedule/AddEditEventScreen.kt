// Defines the file's location within the UI screen architecture for the schedule feature.
package com.example.careconnect.ui.screen.schedule

// Imports necessary libraries for UI components, layout, state management, and ViewModels.
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.careconnect.ui.viewmodel.AddEditEventViewModel
import com.example.careconnect.ui.viewmodel.EventType
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// Opts in to using experimental APIs from Material Design 3.
@OptIn(ExperimentalMaterial3Api::class)
// Defines the main UI composable for the screen used to add or edit schedule entries.
@Composable
fun AddEditEventScreen(
    navController: NavController,
    viewModel: AddEditEventViewModel = hiltViewModel()
) {
    // Collects the screen's state from the ViewModel, rebuilding the UI on changes.
    val state by viewModel.uiState.collectAsState()

    // A side-effect that navigates back automatically after a successful save.
    LaunchedEffect(state.isSaveComplete) {
        if (state.isSaveComplete) {
            navController.popBackStack()
        }
    }

    // Arranges all the UI elements vertically with spacing between them.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creates a formatter to display the date in a user-friendly way.
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        // Displays the main title of the screen, including the selected date.
        Text("Add New Entry for ${state.date.format(formatter)}", style = MaterialTheme.typography.headlineMedium)

        // A component that displays selectable tabs for "Medication" and "Appointment".
        TabRow(selectedTabIndex = state.eventType.ordinal) {
            // The clickable tab for selecting the "Medication" event type.
            Tab(
                selected = state.eventType == EventType.MEDICATION,
                onClick = { viewModel.onEventTypeChange(EventType.MEDICATION) },
                text = { Text("Medication") }
            )
            // The clickable tab for selecting the "Appointment" event type.
            Tab(
                selected = state.eventType == EventType.APPOINTMENT,
                onClick = { viewModel.onEventTypeChange(EventType.APPOINTMENT) },
                text = { Text("Appointment") }
            )
        }

        // Conditionally displays the correct set of input fields based on the selected tab.
        when(state.eventType) {
            // The block of input fields shown when "Medication" is selected.
            EventType.MEDICATION -> {
                OutlinedTextField(value = state.title, onValueChange = viewModel::onTitleChange, label = { Text("Medication Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = state.details, onValueChange = viewModel::onDetailsChange, label = { Text("Dosage (e.g., 10mg)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = state.time, onValueChange = viewModel::onTimeChange, label = { Text("Time (e.g., 9:00 AM)") }, modifier = Modifier.fillMaxWidth())
            }
            // The block of input fields shown when "Appointment" is selected.
            EventType.APPOINTMENT -> {
                OutlinedTextField(value = state.title, onValueChange = viewModel::onTitleChange, label = { Text("Appointment Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = state.location, onValueChange = viewModel::onLocationChange, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = state.time, onValueChange = viewModel::onTimeChange, label = { Text("Time (e.g., 2:30 PM)") }, modifier = Modifier.fillMaxWidth())
            }
        }

        // An expanding spacer that pushes the save button to the bottom of the screen.
        Spacer(modifier = Modifier.weight(1f))

        // The primary button that triggers the save action in the ViewModel.
        Button(
            onClick = viewModel::saveEvent,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Conditionally shows either a loading spinner or the button text.
            if (state.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Save Entry")
            }
        }
    }
}

