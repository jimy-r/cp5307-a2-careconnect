package com.example.careconnect.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.repository.CareRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    var authState by mutableStateOf(AuthState())
        private set

    private val auth: FirebaseAuth = Firebase.auth

    fun login(email: String, pass: String, onLoginSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            authState = authState.copy(error = "Email and password cannot be empty.")
            return
        }
        viewModelScope.launch {
            authState = AuthState(isLoading = true)
            try {
                auth.signInWithEmailAndPassword(email, pass).await()
                authState = AuthState(isLoading = false)
                onLoginSuccess()
            } catch (e: Exception) {
                authState = AuthState(isLoading = false, error = e.message ?: "Login failed.")
            }
        }
    }

    fun register(name: String, email: String, pass: String, onRegistrationSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            authState = authState.copy(error = "All fields are required.")
            return
        }
        viewModelScope.launch {
            authState = AuthState(isLoading = true)
            try {
                // Step 1: Create user in Auth
                val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
                val userId = authResult.user?.uid

                if (userId != null) {
                    // Step 2: Create user profile in Firestore
                    val userProfile = hashMapOf(
                        "uid" to userId,
                        "name" to name,
                        "email" to email,
                        "role" to "Primary Caregiver", // Default role for new registration
                        "careCircleId" to userId // New user becomes their own care circle initially
                    )
                    Firebase.firestore.collection("users").document(userId).set(userProfile).await()
                    authState = AuthState(isLoading = false)
                    onRegistrationSuccess()
                } else {
                    throw Exception("Failed to get user ID.")
                }
            } catch (e: Exception) {
                authState = AuthState(isLoading = false, error = e.message ?: "Registration failed.")
            }
        }
    }

    fun clearError() {
        authState = authState.copy(error = null)
    }
}

