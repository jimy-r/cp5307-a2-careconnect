// Defines the file's location within the local data storage architecture.
package com.example.careconnect.data.local

// Imports the necessary components from the Room library app's models.
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.careconnect.data.local.dao.UserDao
import com.example.careconnect.data.model.User

// Configures this class as the Room database, listing its tables and version.
@Database(entities = [User::class], version = 1, exportSchema = false)
// Defines the abstract database class that Room will implement.
abstract class AppDatabase : RoomDatabase() {
    // Declares a provider for the UserDao interface to access the 'users' table.
    abstract fun userDao(): UserDao
    // Placeholder for adding future DAOs for other tables (e.g., medications).
    // Add other DAOs here
}

