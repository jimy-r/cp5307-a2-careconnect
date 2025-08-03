package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.Appointment
import com.example.careconnect.data.model.Medication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

// A sealed interface to represent different types of events in a single list
sealed interface ScheduleEvent {
    val id: String
    val time: Date

    data class MedicationEvent(val medication: Medication) : ScheduleEvent {
        override val id: String get() = medication.id
        override val time: Date get() = medication.time
    }

    data class AppointmentEvent(val appointment: Appointment) : ScheduleEvent {
        override val id: String get() = appointment.id
        override val time: Date get() = appointment.dateTime
    }
}

data class ScheduleScreenState(
    val selectedDate: Date = Date(),
    val events: List<ScheduleEvent> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ScheduleViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleScreenState())
    val uiState: StateFlow<ScheduleScreenState> = _uiState

    init {
        loadEventsForDate(Date())
    }

    fun onDateSelected(date: Date) {
        _uiState.update { it.copy(selectedDate = date) }
        loadEventsForDate(date)
    }

    private fun loadEventsForDate(date: Date) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Replace with repository.getScheduleForDate(date)
            kotlinx.coroutines.delay(500)

            val calendar = Calendar.getInstance().apply { time = date }
            val dummyMedication = Medication("m1", "Aspirin", "81mg", calendar.apply { set(Calendar.HOUR_OF_DAY, 8) }.time)
            val dummyAppointment = Appointment("a1", "Physical Therapy", "John Doe", "Rehab Center", calendar.apply { set(Calendar.HOUR_OF_DAY, 11) }.time)

            val events = listOf(
                ScheduleEvent.MedicationEvent(dummyMedication),
                ScheduleEvent.AppointmentEvent(dummyAppointment)
            ).sortedBy { it.time }

            _uiState.update { it.copy(isLoading = false, events = events) }
        }
    }

    fun markMedicationAsTaken(medicationId: String, isTaken: Boolean) {
        _uiState.update { currentState ->
            val updatedEvents = currentState.events.map { event ->
                if (event is ScheduleEvent.MedicationEvent && event.id == medicationId) {
                    event.copy(medication = event.medication.copy(isTaken = isTaken))
                } else {
                    event
                }
            }
            currentState.copy(events = updatedEvents)
        }
        // TODO: Call repository to update medication status
    }
}