// Defines the file's location at the root of the application's package structure.
package com.example.careconnect

// Imports the base Application class from the Android framework.
import android.app.Application
// Imports the core FirebaseApp class for manual initialization.
import com.google.firebase.FirebaseApp
// Imports the annotation that enables Hilt dependency injection for the application.
import dagger.hilt.android.HiltAndroidApp

// This annotation tells Hilt to generate the necessary dependency injection components.
@HiltAndroidApp
// Defines a custom Application class, the main entry point of the app process.
class CareConnectApp : Application() {

    // This function is called when the application is first created.
    override fun onCreate() {
        // Calls the parent class's onCreate method to ensure proper setup.
        super.onCreate()
        // Manually initializes all Firebase services, ensuring they are ready for use.
        FirebaseApp.initializeApp(this)
    }
}