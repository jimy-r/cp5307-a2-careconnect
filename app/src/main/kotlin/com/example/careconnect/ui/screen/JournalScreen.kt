// Defines the file's location within the UI screen architecture.
package com.example.careconnect.ui.screen

// Imports necessary libraries for layout, UI components, state management, and ViewModels.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.data.model.JournalEntry
import com.example.careconnect.ui.viewmodel.JournalViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// Defines the main UI composable for the Journal screen.
@Composable
fun JournalScreen(viewModel: JournalViewModel = hiltViewModel()) {
    // Collects the UI-specific state (like dialog visibility) from the ViewModel.
    val state by viewModel.uiState.collectAsState()
    // Collects the real-time list of journal entries from the ViewModel.
    val entries by viewModel.entries.collectAsState()

    // Conditionally displays the "Add Entry" dialog when its state is true.
    if (state.isAddEntryDialogOpen) {
        AddJournalEntryDialog(
            onDismiss = viewModel::closeDialog,
            onConfirm = { note ->
                viewModel.addJournalEntry(note)
            }
        )
    }

    // Sets up the main screen structure with a floating action button.
    Scaffold(
        floatingActionButton = {
            // A button that opens the "Add Entry" dialog when clicked.
            FloatingActionButton(onClick = viewModel::openDialog) {
                Icon(Icons.Default.Add, contentDescription = "Add Journal Entry")
            }
        }
    ) { padding ->
        // Displays a scrollable, efficient list of journal entry cards.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Iterates through the list of entries and creates a card for each one.
            items(entries, key = { it.id ?: it.hashCode() }) { entry ->
                JournalEntryCard(entry = entry)
            }
        }
    }
}

// Defines a reusable UI component for displaying a single journal entry.
@Composable
private fun JournalEntryCard(entry: JournalEntry) {
    // Creates a formatter to display the timestamp in a readable format.
    val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault())
    // A card with a slight shadow to contain the entry's content.
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        // Arranges the text elements of the entry vertically.
        Column(modifier = Modifier.padding(16.dp)) {
            // Displays the formatted date and time of the entry.
            Text(
                text = dateFormat.format(entry.timestamp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Adds a small amount of vertical space.
            Spacer(modifier = Modifier.height(4.dp))
            // Displays the main text content of the journal note.
            Text(
                text = entry.note,
                style = MaterialTheme.typography.bodyLarge
            )
            // Adds more vertical space before the author's name.
            Spacer(modifier = Modifier.height(8.dp))
            // Displays the name of the user who wrote the entry.
            Text(
                text = "— ${entry.authorName}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Defines the pop-up dialog for adding a new journal entry.
@Composable
private fun AddJournalEntryDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    // A state variable to hold the text currently in the input field.
    var text by remember { mutableStateOf("") }
    // The main dialog container that handles dismissal and displays content.
    AlertDialog(
        onDismissRequest = onDismiss,
        // The title text of the dialog.
        title = { Text("Add Journal Entry") },
        // The main content of the dialog, containing the text input field.
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        // The confirmation button that saves the new entry.
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Save")
            }
        },
        // The dismissal button that cancels the action.
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

