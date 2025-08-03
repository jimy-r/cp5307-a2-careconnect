package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.careconnect.data.model.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import javax.inject.Inject

data class JournalScreenState(
    val entries: List<JournalEntry> = emptyList(),
    val isAddEntryDialogOpen: Boolean = false
)

@HiltViewModel
class JournalViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(JournalScreenState())
    val uiState: StateFlow<JournalScreenState> = _uiState

    // ✅ MOVED THIS BLOCK to be defined BEFORE the init block uses it.
    private val dummyEntries = listOf(
        JournalEntry("j2", "Sarah", "Mom seemed a little tired today but enjoyed her walk.", Date(System.currentTimeMillis() - 86400000)),
        JournalEntry("j1", "Mark", "Successful visit with Dr. Smith. New prescription details are in the meds list.", Date(System.currentTimeMillis() - 172800000))
    )

    init {
        // This line will now work correctly because dummyEntries has a value.
        _uiState.update { it.copy(entries = dummyEntries) }
    }

    fun openDialog() {
        _uiState.update { it.copy(isAddEntryDialogOpen = true) }
    }

    fun closeDialog() {
        _uiState.update { it.copy(isAddEntryDialogOpen = false) }
    }

    fun addJournalEntry(note: String) {
        val newEntry = JournalEntry(
            id = (Math.random() * 1000).toString(),
            authorName = "Mark (You)", // TODO: Get current user's name
            note = note,
            timestamp = Date()
        )
        _uiState.update { currentState ->
            currentState.copy(
                entries = listOf(newEntry) + currentState.entries
            )
        }
        closeDialog()
        // TODO: Call repository.addJournalEntry(note)
    }
}