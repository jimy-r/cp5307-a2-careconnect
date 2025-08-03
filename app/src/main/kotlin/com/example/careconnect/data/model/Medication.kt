// Defines the file's location within the data model architecture of the app.
package com.example.careconnect.data.model

// Imports the annotation to link a field to the Firestore document ID.
import com.google.firebase.firestore.DocumentId
// Imports the standard Java class for handling date and time information.
import java.util.Date

// Defines the blueprint for a 'Medication' object with its properties.
data class Medication(
    // Tells Firestore to automatically populate this field with its document ID.
    @DocumentId
    // Stores the unique ID of the medication entry, which can be null initially.
    var id: String? = null,
    // Stores the name of the medication (e.g., "Aspirin").
    val name: String = "",
    // Stores the dosage information for the medication (e.g., "81mg").
    val dosage: String = "",
    // Stores the specific date and time the medication is scheduled to be taken.
    val time: Date = Date(),
    // Stores a true/false value indicating if the medication has been administered.
    val isTaken: Boolean = false,
    // Stores the name of the caregiver who administered the medication, if applicable.
    val administeredBy: String? = null
)

