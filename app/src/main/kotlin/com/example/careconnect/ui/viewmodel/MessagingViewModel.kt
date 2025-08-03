package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.Message
import com.example.careconnect.data.repository.CareRepository // <-- ADDED import
import com.google.firebase.auth.ktx.auth                     // <-- ADDED import
import com.google.firebase.ktx.Firebase                        // <-- ADDED import
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// This data class is now simplified to only hold the text input state.
data class MessagingScreenState(
    val currentInput: String = ""
)

@HiltViewModel
// Inject the CareRepository
class MessagingViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MessagingScreenState())
    val uiState: StateFlow<MessagingScreenState> = _uiState

    // Provides the current user's ID so the UI can align messages correctly.
    val currentUserId: String? = Firebase.auth.currentUser?.uid

    // This StateFlow directly connects to the repository's real-time listener.
    // The UI will collect this to get a live list of messages.
    val messages: StateFlow<List<Message>> = repository.getMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onInputChanged(newInput: String) {
        _uiState.update { it.copy(currentInput = newInput) }
    }

    // This now calls the real repository function to save the message to Firestore.
    fun sendMessage() {
        val text = _uiState.value.currentInput
        if (text.isNotBlank()) {
            viewModelScope.launch {
                repository.sendMessage(text)
            }
            // Clear the input field immediately for a responsive feel.
            _uiState.update { it.copy(currentInput = "") }
        }
    }
}

