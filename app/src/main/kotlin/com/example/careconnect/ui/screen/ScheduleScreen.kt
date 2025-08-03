// Defines the file's location within the UI screen architecture.
package com.example.careconnect.ui.screen

// Imports necessary libraries for layout, UI components, navigation, and the calendar.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.careconnect.ui.navigation.Screen
import com.example.careconnect.ui.viewmodel.ScheduleEvent
import com.example.careconnect.ui.viewmodel.ScheduleViewModel
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import java.text.SimpleDateFormat
import java.util.*

// Defines the main UI composable for the Schedule screen.
@Composable
fun ScheduleScreen(
    navController: NavController,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    // Collects the screen's state from the ViewModel, rebuilding the UI on changes.
    val state by viewModel.uiState.collectAsState()

    // Creates and remembers the state for the calendar UI component.
    val calendarState = rememberSelectableCalendarState(
        initialSelection = listOf(state.selectedDate),
        initialSelectionMode = SelectionMode.Single,
    )

    // A side-effect that updates the ViewModel whenever the user selects a new date.
    LaunchedEffect(calendarState.selectionState.selection) {
        calendarState.selectionState.selection.firstOrNull()?.let {
            viewModel.onDateSelected(it)
        }
    }

    // Sets up the main screen structure with a floating action button.
    Scaffold(
        floatingActionButton = {
            // A button that navigates to the "Add Event" screen with the selected date.
            FloatingActionButton(onClick = {
                val selectedDateEpochDay = state.selectedDate.toEpochDay()
                navController.navigate(Screen.AddEditEvent.createRoute(selectedDateEpochDay))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule Entry")
            }
        }
    ) { padding ->
        // Arranges the calendar and the event list vertically.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Displays the interactive calendar component from the external library.
            SelectableCalendar(
                modifier = Modifier.padding(8.dp),
                calendarState = calendarState,
            )

            // A visual separator line between the calendar and the event list.
            Divider()

            // Conditionally displays a loading spinner, an empty message, or the list of events.
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.eventsForSelectedDate.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No events scheduled for this day.")
                }
            } else {
                // Displays a scrollable, efficient list of event cards for the selected day.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Iterates through the list and displays the correct item type for each event.
                    items(state.eventsForSelectedDate, key = { it.id }) { event ->
                        when (event) {
                            is ScheduleEvent.AppointmentEvent -> AppointmentItem(appointment = event.appointment)
                            is ScheduleEvent.MedicationEvent -> MedicationItem(medication = event.medication)
                        }
                    }
                }
            }
        }
    }
}

// Defines a reusable UI component for displaying a single appointment.
@Composable
private fun AppointmentItem(appointment: com.example.careconnect.data.model.Appointment) {
    // Creates a formatter to display the time in a readable format.
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    // A card to contain the appointment's details.
    Card(modifier = Modifier.fillMaxWidth()) {
        // Arranges the icon and text details horizontally.
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.EventNote, null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(16.dp))
            // Arranges the appointment's textual information vertically.
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(appointment.title, style = MaterialTheme.typography.titleMedium)
                if (appointment.location.isNotBlank()) {
                    Text(appointment.location, style = MaterialTheme.typography.bodyMedium)
                }
                if (appointment.dateTime != null) {
                    Text(
                        timeFormat.format(appointment.dateTime),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Defines a reusable UI component for displaying a single medication.
@Composable
private fun MedicationItem(medication: com.example.careconnect.data.model.Medication) {
    // Creates a formatter to display the time in a readable format.
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    // A card to contain the medication's details.
    Card(modifier = Modifier.fillMaxWidth()) {
        // Arranges the icon and text details horizontally.
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Medication, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            // Arranges the medication's textual information vertically.
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(medication.name, style = MaterialTheme.typography.titleMedium)
                if (medication.dosage.isNotBlank()) {
                    Text(medication.dosage, style = MaterialTheme.typography.bodyMedium)
                }
                if (medication.time != null) {
                    Text(
                        timeFormat.format(medication.time),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
