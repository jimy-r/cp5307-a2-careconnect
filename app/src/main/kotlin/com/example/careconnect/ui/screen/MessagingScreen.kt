// Defines the file's location within the UI screen architecture.
package com.example.careconnect.ui.screen

// Imports necessary libraries for layout, UI components, state management, and ViewModels.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.data.model.Message
import com.example.careconnect.ui.viewmodel.MessagingViewModel
import kotlinx.coroutines.launch

// Defines the main UI composable for the real-time Messaging screen.
@Composable
fun MessagingScreen(viewModel: MessagingViewModel = hiltViewModel()) {
    // Collects the UI-specific state (like text input) from the ViewModel.
    val state by viewModel.uiState.collectAsState()
    // Collects the real-time list of messages from the ViewModel.
    val messages by viewModel.messages.collectAsState()
    // Retrieves the current user's ID to determine message alignment.
    val currentUserId = viewModel.currentUserId

    // A state object to control the scroll position of the message list.
    val listState = rememberLazyListState()
    // A coroutine scope tied to the composable's lifecycle for launching animations.
    val coroutineScope = rememberCoroutineScope()

    // A side-effect that automatically scrolls to the bottom when a new message is added.
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.lastIndex)
            }
        }
    }

    // Sets up the main screen structure with a bottom bar for message input.
    Scaffold(
        bottomBar = {
            // The custom composable for the text input field and send button.
            MessageInputBar(
                text = state.currentInput,
                onTextChange = viewModel::onInputChanged,
                onSendClick = viewModel::sendMessage
            )
        }
    ) { padding ->
        // Displays a scrollable, efficient list of chat bubbles.
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Iterates through the list of messages and creates a chat bubble for each one.
            items(messages, key = { it.id ?: it.hashCode() }) { message ->
                ChatBubble(message = message, currentUserId = currentUserId)
            }
        }
    }
}

// Defines the reusable UI component for the message input bar at the bottom.
@Composable
private fun MessageInputBar(text: String, onTextChange: (String) -> Unit, onSendClick: () -> Unit) {
    // A surface with a shadow to lift the input bar off the background.
    Surface(shadowElevation = 8.dp) {
        // Arranges the text field and send button horizontally.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // The text input field where the user types their message.
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
            )
            // Adds space between the text field and the button.
            Spacer(modifier = Modifier.width(8.dp))
            // The icon button that sends the message when clicked.
            IconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

// Defines the reusable UI component for a single chat bubble.
@Composable
private fun ChatBubble(message: Message, currentUserId: String?) {
    // Determines if the message was sent by the currently logged-in user.
    val isFromCurrentUser = message.senderId == currentUserId
    // Sets the alignment to right for the current user, and left for others.
    val alignment = if (isFromCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    // Sets a different background color for outgoing versus incoming messages.
    val backgroundColor = if (isFromCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    // Sets a different text color for outgoing versus incoming messages.
    val textColor = if (isFromCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    // A Box that fills the width and uses the calculated alignment.
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        // A card with rounded corners to serve as the chat bubble.
        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            // A Column to arrange the sender's name (if shown) and the message text.
            Column(modifier = Modifier.padding(12.dp)) {
                // Conditionally displays the sender's name for incoming messages only.
                if (!isFromCurrentUser) {
                    Text(message.senderName, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                // Displays the main text content of the message.
                Text(text = message.text, style = MaterialTheme.typography.bodyLarge, color = textColor)
            }
        }
    }
}

