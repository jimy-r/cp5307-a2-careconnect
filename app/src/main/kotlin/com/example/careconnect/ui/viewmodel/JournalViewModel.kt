// Defines the file's location within the ViewModel architecture of the app.
package com.example.careconnect.ui.viewmodel

// Imports necessary libraries for ViewModel creation, coroutines, and the data layer.
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.JournalEntry
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Defines the data structure for UI-specific state on the Journal screen.
data class JournalScreenState(
    val isAddEntryDialogOpen: Boolean = false
)

// Marks this class as a ViewModel that can be injected by Hilt.
@HiltViewModel
// Defines the ViewModel for the Journal screen, injecting the data repository.
class JournalViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    // A private, mutable state flow that holds the current UI state.
    private val _uiState = MutableStateFlow(JournalScreenState())
    // An immutable, public-facing state flow that the UI can safely observe for UI state.
    val uiState: StateFlow<JournalScreenState> = _uiState

    // A public state flow that directly connects to the repository's real-time data.
    val entries: StateFlow<List<JournalEntry>> = repository.getJournalEntries()
        // Converts the cold Flow from the repository into a hot StateFlow.
        .stateIn(
            // Defines the coroutine scope in which the upstream flow is shared.
            scope = viewModelScope,
            // Starts collecting from the repository only when the UI is visible.
            started = SharingStarted.WhileSubscribed(5000),
            // Provides an initial empty list while the first database query is in progress.
            initialValue = emptyList()
        )

    // A public function for the UI to call to show the "Add Entry" dialog.
    fun openDialog() {
        // Updates the state to set the dialog visibility to true.
        _uiState.update { it.copy(isAddEntryDialogOpen = true) }
    }

    // A public function for the UI to call to hide the "Add Entry" dialog.
    fun closeDialog() {
        // Updates the state to set the dialog visibility to false.
        _uiState.update { it.copy(isAddEntryDialogOpen = false) }
    }

    // A public function that saves a new journal entry to the database.
    fun addJournalEntry(note: String) {
        // Ensures that an empty note is not saved.
        if (note.isNotBlank()) {
            // Launches a coroutine to call the repository's save function off the main thread.
            viewModelScope.launch {
                repository.addJournalEntry(note)
            }
        }
        // Closes the input dialog after the save process has been initiated.
        closeDialog()
    }
}