package com.example.careconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.ui.components.LoadingIndicator
import com.example.careconnect.ui.viewmodel.ScheduleEvent
import com.example.careconnect.ui.viewmodel.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Simple Date Header - A real app would use a calendar view component
        Text(
            text = dateFormat.format(state.selectedDate),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else if (state.events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No events scheduled for this day.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.events, key = { it.id }) { event ->
                    when (event) {
                        is ScheduleEvent.AppointmentEvent -> AppointmentItem(
                            appointment = event.appointment,
                            timeFormat = timeFormat
                        )
                        is ScheduleEvent.MedicationEvent -> MedicationItem(
                            medication = event.medication,
                            timeFormat = timeFormat,
                            onStatusChange = { isTaken ->
                                viewModel.markMedicationAsTaken(event.medication.id, isTaken)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentItem(appointment: com.example.careconnect.data.model.Appointment, timeFormat: SimpleDateFormat) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.EventNote, null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(appointment.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${timeFormat.format(appointment.dateTime)} at ${appointment.location}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MedicationItem(
    medication: com.example.careconnect.data.model.Medication,
    timeFormat: SimpleDateFormat,
    onStatusChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Medication, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(medication.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${medication.dosage} at ${timeFormat.format(medication.time)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Checkbox(
                checked = medication.isTaken,
                onCheckedChange = onStatusChange
            )
        }
    }
}
