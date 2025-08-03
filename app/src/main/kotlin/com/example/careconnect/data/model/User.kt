// Defines the file's location within the data model architecture of the app.
package com.example.careconnect.data.model

// Imports the necessary annotations from the Room library for database mapping.
import androidx.room.Entity
import androidx.room.PrimaryKey

// Marks this data class as a table named "users" in the local Room database.
@Entity(tableName = "users")
// Defines the blueprint for a 'User' object with its properties.
data class User(
    // Designates the 'uid' field as the unique primary key for each row in the table.
    @PrimaryKey val uid: String = "",
    // Stores the full display name of the user.
    val name: String = "",
    // Stores the user's email address, used for login and contact.
    val email: String = "",
    // Stores the user's role within the care circle (e.g., "Primary Caregiver").
    val role: String = "",
    // Stores the ID that links this user to a specific shared care group.
    val careCircleId: String = "",
    // Stores the user's contact phone number.
    val phone: String = "",
    // Stores the user's physical address.
    val address: String = ""
)

