// Defines the file's location within the ViewModel architecture of the app.
package com.example.careconnect.ui.viewmodel

// Imports necessary libraries for ViewModel creation, coroutines, and the data layer.
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.Appointment
import com.example.careconnect.data.model.Medication
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

// Defines a sealed interface to represent different event types in a single, unified list.
sealed interface ScheduleEvent {
    val id: String
    val time: Date

    // A data class representing a medication event, conforming to the ScheduleEvent interface.
    data class MedicationEvent(val medication: Medication) : ScheduleEvent {
        override val id: String get() = medication.id ?: medication.hashCode().toString()
        override val time: Date get() = medication.time
    }

    // A data class representing an appointment event, conforming to the ScheduleEvent interface.
    data class AppointmentEvent(val appointment: Appointment) : ScheduleEvent {
        override val id: String get() = appointment.id ?: appointment.hashCode().toString()
        override val time: Date get() = appointment.dateTime
    }
}

// Defines the data structure for all the state information on the Schedule screen.
data class ScheduleScreenState(
    val selectedDate: LocalDate = LocalDate.now(),
    val eventsForSelectedDate: List<ScheduleEvent> = emptyList(),
    val isLoading: Boolean = false
)

// Marks this class as a ViewModel that can be injected by Hilt.
@HiltViewModel
// Defines the ViewModel for the Schedule screen, injecting the data repository.
class ScheduleViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    // A private, mutable state flow that holds the current state of the screen.
    private val _uiState = MutableStateFlow(ScheduleScreenState())
    // An immutable, public-facing state flow that the UI can safely observe.
    val uiState: StateFlow<ScheduleScreenState> = _uiState.asStateFlow()

    // This block runs when the ViewModel is first created to set up the data flow.
    init {
        // Launches a coroutine to handle the reactive data loading pipeline.
        viewModelScope.launch {
            // Begins a reactive chain that listens for changes to the UI state.
            _uiState
                // Extracts only the 'selectedDate' from the state, as that's what we're interested in.
                .map { it.selectedDate }
                // Prevents re-fetching data if the same date is selected again.
                .distinctUntilChanged()
                // Automatically cancels the old database query and starts a new one when the date changes.
                .flatMapLatest { date ->
                    // Sets the loading state to true before fetching new data.
                    _uiState.update { it.copy(isLoading = true) }
                    // Converts the modern LocalDate to a legacy Date object for the repository.
                    val utilDate = java.sql.Date.valueOf(date.toString())
                    // Calls the repository to get the raw schedule data for the new date.
                    repository.getDailySchedule(utilDate)
                }
                // Transforms the raw DailySchedule object from the repository into a UI-friendly list.
                .map { dailySchedule ->
                    val combinedList = mutableListOf<ScheduleEvent>()
                    dailySchedule.appointments.forEach { combinedList.add(ScheduleEvent.AppointmentEvent(it)) }
                    dailySchedule.medications.forEach { combinedList.add(ScheduleEvent.MedicationEvent(it)) }
                    combinedList.sortBy { it.time }
                    combinedList
                }
                // Collects the final, transformed list of events and updates the UI state.
                .collect { events ->
                    _uiState.update {
                        it.copy(
                            eventsForSelectedDate = events,
                            isLoading = false
                        )
                    }
                }
        }
    }

    // A public function for the UI to call when the user selects a new date on the calendar.
    fun onDateSelected(date: LocalDate) {
        // Updates the state with the newly selected date, which triggers the reactive data fetch.
        _uiState.update { it.copy(selectedDate = date) }
    }
}