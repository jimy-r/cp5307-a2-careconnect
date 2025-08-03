// Defines the file's location within the data model architecture of the app.
package com.example.careconnect.data.model

// Imports the annotation to link a field to the Firestore document ID.
import com.google.firebase.firestore.DocumentId
// Imports the standard Java class for handling date and time information.
import java.util.Date

// Defines the blueprint for an 'Appointment' object with its properties.
data class Appointment(
    // Tells Firestore to automatically populate this field with its document ID.
    @DocumentId
    // Stores the unique ID of the appointment, which can be null initially.
    var id: String? = null,
    // Stores the main title or purpose of the appointment.
    val title: String = "",
    // Stores the name of the specialist or doctor for the appointment.
    val specialist: String = "",
    // Stores the physical or virtual location of the appointment.
    val location: String = "",
    // Stores the specific date and time when the appointment is scheduled.
    val dateTime: Date = Date()
)

