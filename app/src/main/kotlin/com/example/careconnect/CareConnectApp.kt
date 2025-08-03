package com.example.careconnect

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CareConnectApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Manually initialize FirebaseApp.
        // This ensures Firebase is ready before any other component needs it.
        FirebaseApp.initializeApp(this)
    }
}