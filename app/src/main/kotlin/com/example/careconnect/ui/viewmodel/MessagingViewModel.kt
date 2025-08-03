package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class MessagingScreenState(
    val messages: List<Message> = emptyList(),
    val currentInput: String = ""
)

@HiltViewModel
class MessagingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MessagingScreenState())
    val uiState: StateFlow<MessagingScreenState> = _uiState

    init {
        loadMessages()
    }

    private fun loadMessages() {
        // TODO: Replace with a flow from repository.getMessages()
        _uiState.update {
            it.copy(
                messages = listOf(
                    Message("1", "Sarah", "Hi Mark, just checking in. How's Mom today?", Date(System.currentTimeMillis() - 100000), false),
                    Message("2", "Mark (You)", "Hey Sarah! She's doing well. Took her 8am meds on time.", Date(System.currentTimeMillis() - 50000), true)
                )
            )
        }
    }

    fun onInputChanged(newInput: String) {
        _uiState.update { it.copy(currentInput = newInput) }
    }

    fun sendMessage() {
        val text = _uiState.value.currentInput
        if (text.isBlank()) return

        val newMessage = Message(
            id = (Math.random() * 1000).toString(),
            senderName = "Mark (You)",
            text = text,
            timestamp = Date(),
            isFromCurrentUser = true
        )

        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + newMessage,
                currentInput = ""
            )
        }

        // TODO: Call repository.sendMessage(text)
    }
}

