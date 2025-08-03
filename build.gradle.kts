// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Defines the Android Application plugin, making it available to modules.
    id("com.android.application") version "8.10.1" apply false // Use a recent stable version

    // Defines the Kotlin Android plugin.
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false // Match your Kotlin version

    // Defines the Google Services plugin for Firebase.
    id("com.google.gms.google-services") version "4.4.2" apply false // Use the latest version

    // Defines the Hilt plugin for dependency injection.
    id("com.google.dagger.hilt.android") version "2.51.1" apply false

    // Defines the KSP plugin for code generation (used by Room and Hilt).
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false // Match your Kotlin version
}