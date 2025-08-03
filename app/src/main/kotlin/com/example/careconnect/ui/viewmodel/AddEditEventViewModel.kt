// Defines the file's location within the ViewModel architecture of the app.
package com.example.careconnect.ui.viewmodel

// Imports necessary libraries for ViewModel creation, coroutines, and data handling.
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.Appointment
import com.example.careconnect.data.model.Medication
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// Defines the two possible types of events a user can create.
enum class EventType { MEDICATION, APPOINTMENT }

// Defines the data structure for all the state information on the "Add Event" screen.
data class AddEventState(
    val eventType: EventType = EventType.MEDICATION,
    val title: String = "",
    val details: String = "",
    val location: String = "",
    val time: String = "",
    val date: LocalDate = LocalDate.now(),
    val isSaving: Boolean = false,
    val isSaveComplete: Boolean = false
)

// Marks this class as a ViewModel that can be injected by Hilt.
@HiltViewModel
// Defines the ViewModel, injecting the repository and a handle to saved navigation state.
class AddEditEventViewModel @Inject constructor(
    private val repository: CareRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // A private, mutable state flow that holds the current state of the screen.
    private val _uiState = MutableStateFlow(AddEventState())
    // An immutable, public-facing state flow that the UI can safely observe.
    val uiState: StateFlow<AddEventState> = _uiState.asStateFlow()

    // This block runs when the ViewModel is first created to initialize its state.
    init {
        // Retrieves the 'date' argument that was passed via navigation.
        val dateEpochDay: Long? = savedStateHandle.get("date")
        // Updates the initial state with the selected date if it exists.
        if (dateEpochDay != null && dateEpochDay > 0) {
            val selectedDate = LocalDate.ofEpochDay(dateEpochDay)
            _uiState.update { it.copy(date = selectedDate) }
        }
    }

    // A series of public functions for the UI to call to update the state.
    // --- Action Handlers ---
    fun onEventTypeChange(type: EventType) { _uiState.update { it.copy(eventType = type) } }
    fun onTitleChange(title: String) { _uiState.update { it.copy(title = title) } }
    fun onDetailsChange(details: String) { _uiState.update { it.copy(details = details) } }
    fun onLocationChange(location: String) { _uiState.update { it.copy(location = location) } }
    fun onTimeChange(time: String) { _uiState.update { it.copy(time = time) } }

    // This function handles the logic for saving the new event to the database.
    fun saveEvent() {
        // Launches a coroutine to perform the database write operation off the main thread.
        viewModelScope.launch {
            // Updates the UI state to show a loading indicator.
            _uiState.update { it.copy(isSaving = true) }
            // Gets a snapshot of the current state to work with.
            val state = _uiState.value

            // Combines the selected date with the entered time string into a single Date object.
            val eventDateTime = combineDateAndTime(state.date, state.time)

            // Determines whether to save a Medication or an Appointment based on the event type.
            when (state.eventType) {
                // If the type is Medication, create a Medication object and save it.
                EventType.MEDICATION -> {
                    val newMed = Medication(name = state.title, dosage = state.details, time = eventDateTime)
                    repository.addMedication(newMed)
                }
                // If the type is Appointment, create an Appointment object and save it.
                EventType.APPOINTMENT -> {
                    val newAppt = Appointment(title = state.title, specialist = state.details, location = state.location, dateTime = eventDateTime)
                    repository.addAppointment(newAppt)
                }
            }
            // Updates the UI state to indicate that the save process is complete.
            _uiState.update { it.copy(isSaving = false, isSaveComplete = true) }
        }
    }

    // A private helper function to parse a time string and combine it with a date.
    private fun combineDateAndTime(date: LocalDate, timeString: String): Date {
        // Creates a Calendar instance and sets its date from the LocalDate.
        val calendar = Calendar.getInstance()
        calendar.set(date.year, date.monthValue - 1, date.dayOfMonth)

        // Attempts to parse the user's time input string.
        try {
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val parsedTime = timeFormat.parse(timeString)
            if (parsedTime != null) {
                val timeCalendar = Calendar.getInstance().apply { time = parsedTime }
                calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            } else {
                // Defaults to midnight if the parsed time is null.
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
            }
            // Catches any parsing errors and defaults to midnight.
        } catch (e: Exception) {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
        }
        // Resets seconds and milliseconds to ensure clean time data.
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        // Returns the final combined Date object.
        return calendar.time
    }
}

