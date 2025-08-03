// Defines the file's location within the ViewModel architecture of the app.
package com.example.careconnect.ui.viewmodel

// Imports necessary libraries for ViewModel creation, coroutines, and the data layer.
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.User
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Marks this class as a ViewModel that can be injected by Hilt.
@HiltViewModel
// Defines the ViewModel for the Admin screen, injecting the data repository.
class AdminViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    // A private, mutable state flow that holds the current user's profile data.
    private val _userProfile = MutableStateFlow<User?>(null)
    // An immutable, public-facing state flow that the UI can safely observe.
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    // This block runs when the ViewModel is first created.
    init {
        // Launches a coroutine to fetch the user's profile from the repository.
        viewModelScope.launch {
            // Collects the user profile data and updates the state flow.
            repository.getCurrentUserProfile().collect { user ->
                _userProfile.value = user
            }
        }
    }

    // A public function for the UI to call to sign the user out.
    fun signOut() {
        // Delegates the sign-out logic to the repository.
        repository.signOut()
    }
}

