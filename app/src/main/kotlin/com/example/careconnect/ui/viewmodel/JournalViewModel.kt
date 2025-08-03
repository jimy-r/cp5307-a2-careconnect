package com.example.careconnect.ui.viewmodel

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

// This data class is now simplified. The list of entries will come from a separate StateFlow.
data class JournalScreenState(
    val isAddEntryDialogOpen: Boolean = false
)

@HiltViewModel
// Inject the CareRepository
class JournalViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(JournalScreenState())
    val uiState: StateFlow<JournalScreenState> = _uiState

    // ✅ This StateFlow directly connects to the repository's real-time listener.
    // The UI will collect this to get a live list of journal entries.
    val entries: StateFlow<List<JournalEntry>> = repository.getJournalEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun openDialog() {
        _uiState.update { it.copy(isAddEntryDialogOpen = true) }
    }

    fun closeDialog() {
        _uiState.update { it.copy(isAddEntryDialogOpen = false) }
    }

    // This calls the real repository function to save the entry to Firestore.
    fun addJournalEntry(note: String) {
        if (note.isNotBlank()) {
            viewModelScope.launch {
                repository.addJournalEntry(note)
            }
        }
        closeDialog()
    }
}