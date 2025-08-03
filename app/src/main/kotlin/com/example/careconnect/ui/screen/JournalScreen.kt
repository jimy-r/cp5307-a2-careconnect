package com.example.careconnect.ui.screen

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

@Composable
fun JournalScreen(viewModel: JournalViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val entries by viewModel.entries.collectAsState()

    if (state.isAddEntryDialogOpen) {
        AddJournalEntryDialog(
            onDismiss = viewModel::closeDialog,
            onConfirm = { note ->
                viewModel.addJournalEntry(note)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::openDialog) {
                Icon(Icons.Default.Add, contentDescription = "Add Journal Entry")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(entries, key = { it.id ?: it.hashCode() }) { entry ->
                JournalEntryCard(entry = entry)
            }
        }
    }
}

@Composable
private fun JournalEntryCard(entry: JournalEntry) {
    val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault())
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = dateFormat.format(entry.timestamp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = entry.note,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "— ${entry.authorName}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AddJournalEntryDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Journal Entry") },
        text = {
            OutlinedTextField(
                value = text,
                // ✅ THIS IS THE FIXED LINE (hyphen removed)
                onValueChange = { text = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

