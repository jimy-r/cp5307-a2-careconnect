// Defines the file's location within the data model architecture of the app.
package com.example.careconnect.data.model

// Imports the annotation to link a field to the Firestore document ID.
import com.google.firebase.firestore.DocumentId
// Imports the standard Java class for handling date and time information.
import java.util.Date

// Defines the blueprint for a 'Message' object with its properties.
data class Message(
    // Tells Firestore to automatically populate this field with its document ID.
    @DocumentId
    // Stores the unique ID of the message, which can be null initially.
    var id: String? = null,
    // Stores the unique ID of the user who sent the message.
    val senderId: String = "",
    // Stores the display name of the user who sent the message.
    val senderName: String = "",
    // Stores the text content of the message itself.
    val text: String = "",
    // Stores the specific date and time when the message was sent.
    val timestamp: Date = Date()
)

