// Defines the file's location within the data model architecture of the app.
package com.example.careconnect.data.model

// Imports the annotation to link a field to the Firestore document ID.
import com.google.firebase.firestore.DocumentId
// Imports the annotation to exclude a field from being saved to Firestore.
import com.google.firebase.firestore.Exclude
// Imports the standard Java class for handling date and time information.
import java.util.Date

// Defines the blueprint for a 'JournalEntry' object with its properties.
data class JournalEntry(
    // Tells Firestore to automatically populate this field with its document ID when reading.
    @DocumentId
    // Stores the unique ID of the journal entry, which can be null initially.
    var id: String? = null,

    // Stores the name of the user who wrote the journal entry.
    val authorName: String = "",
    // Stores the main text content of the journal note.
    val note: String = "",
    // Stores the specific date and time when the journal entry was created.
    val timestamp: Date = Date()
) {
    // This annotation prevents the `toMap()` function itself from being saved to Firestore.
    @Exclude
    // Provides a custom map for Firestore to use when writing, excluding the 'id' field.
    fun toMap(): Map<String, Any?> {
        // Returns a map containing only the fields that should be saved to the database.
        return mapOf(
            "authorName" to authorName,
            "note" to note,
            "timestamp" to timestamp
        )
    }
}
