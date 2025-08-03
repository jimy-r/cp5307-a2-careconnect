// Defines the file's location within the ViewModel architecture of the app.
package com.example.careconnect.ui.viewmodel

// Imports necessary libraries for ViewModel creation, coroutines, and the data layer.
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Defines the data structure for all the state information on the Profile screen.
data class ProfileScreenState(
    val currentName: String = "",
    val currentPhone: String = "",
    val currentAddress: String = "",
    val currentRole: String = "",
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val isDeleteSuccess: Boolean = false,
    val isConfirmDeleteDialogOpen: Boolean = false
)

// Marks this class as a ViewModel that can be injected by Hilt.
@HiltViewModel
// Defines the ViewModel for the Profile screen, injecting the data repository.
class ProfileViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    // A private, mutable state flow that holds the current state of the screen.
    private val _uiState = MutableStateFlow(ProfileScreenState())
    // An immutable, public-facing state flow that the UI can safely observe.
    val uiState: StateFlow<ProfileScreenState> = _uiState.asStateFlow()

    // This block runs when the ViewModel is first created to pre-populate the fields.
    init {
        // Launches a coroutine to fetch the user's current profile from the repository.
        viewModelScope.launch {
            // Collects the user profile data and updates the state with the current values.
            repository.getCurrentUserProfile().collect { user ->
                _uiState.update {
                    it.copy(
                        currentName = user?.name ?: "",
                        currentPhone = user?.phone ?: "",
                        currentAddress = user?.address ?: "",
                        currentRole = user?.role ?: ""
                    )
                }
            }
        }
    }

    // A series of public functions for the UI to call to update the state in real-time.
    // --- Action Handlers ---
    fun onNameChange(newName: String) { _uiState.update { it.copy(currentName = newName) } }
    fun onPhoneChange(newPhone: String) { _uiState.update { it.copy(currentPhone = newPhone) } }
    fun onAddressChange(newAddress: String) { _uiState.update { it.copy(currentAddress = newAddress) } }
    fun onRoleChange(newRole: String) { _uiState.update { it.copy(currentRole = newRole) } }
    fun showDeleteDialog() { _uiState.update { it.copy(isConfirmDeleteDialogOpen = true) } }
    fun hideDeleteDialog() { _uiState.update { it.copy(isConfirmDeleteDialogOpen = false) } }

    // This function handles the logic for saving the updated profile to the database.
    fun saveProfile() {
        // Launches a coroutine to perform the database write operation off the main thread.
        viewModelScope.launch {
            // Updates the UI state to show a loading indicator.
            _uiState.update { it.copy(isLoading = true) }
            // Gets a snapshot of the current state to save.
            val currentState = _uiState.value
            // Calls the repository to update the user's profile in Firestore.
            repository.updateUserProfile(
                name = currentState.currentName,
                phone = currentState.currentPhone,
                address = currentState.currentAddress,
                role = currentState.currentRole
            )
            // Updates the UI state to indicate that the save process is complete.
            _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
        }
    }

    // This function handles the logic for permanently deleting the user's account.
    fun deleteProfile() {
        // Launches a coroutine to perform the deletion off the main thread.
        viewModelScope.launch {
            // Hides the confirmation dialog.
            hideDeleteDialog()
            // Updates the UI state to show a loading indicator.
            _uiState.update { it.copy(isLoading = true) }
            // Calls the repository to delete the user's data and authentication record.
            repository.deleteUserAccount()
            // Updates the UI state to indicate that the deletion is complete.
            _uiState.update { it.copy(isLoading = false, isDeleteSuccess = true) }
        }
    }
}

