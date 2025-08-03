package com.example.careconnect.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude // <-- ADD THIS IMPORT
import java.util.Date

data class JournalEntry(
    @DocumentId
    var id: String? = null,

    // The rest of the fields should have default values for safety.
    val authorName: String = "",
    val note: String = "",
    val timestamp: Date = Date()
) {
    // This function tells Firestore to ignore the 'id' field when WRITING data,
    // which prevents the 'id: null' field from being created in the document.
    // The @DocumentId annotation will still work when READING data.
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "authorName" to authorName,
            "note" to note,
            "timestamp" to timestamp
        )
    }
}
